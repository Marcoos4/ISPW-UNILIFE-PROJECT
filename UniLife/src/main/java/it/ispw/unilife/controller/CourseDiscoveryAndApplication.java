package it.ispw.unilife.controller;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.boundary.CertificateValidationBoundary;
import it.ispw.unilife.dao.ApplicationDAO;
import it.ispw.unilife.dao.CourseDAO;
import it.ispw.unilife.dao.factory.DAOFactory;
import it.ispw.unilife.enums.ApplicationEvaluation;
import it.ispw.unilife.enums.ApplicationStatus;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.enums.RequirementType;
import it.ispw.unilife.exception.*;
import it.ispw.unilife.model.*;
import it.ispw.unilife.model.admission.*;
import it.ispw.unilife.model.session.Session;
import it.ispw.unilife.model.session.SessionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CourseDiscoveryAndApplication {

    private static final Logger LOGGER = Logger.getLogger((CourseDiscoveryAndApplication.class.getName()));


    //==================================================================================
    // PUBLIC INTERFACE - SEARCH & DISCOVERY
    // ==================================================================================

    public List<FilterCourseBean> listSearchFilter() {
        List<FilterCourseBean> beanList = new ArrayList<>();
        try {
            List<Course> allCourses = getCourseDAO().getAll();
            for (Course course : allCourses) {
                beanList.add(mapCourseToFilterBean(course));
            }
        } catch (DAOException | UserNotFoundException e) {
            e.printStackTrace();
        }
        return beanList;
    }

    public List<CourseBean> searchCoursesByFilters(FilterCourseBean filterInput) {
        List<CourseBean> beanList = new ArrayList<>();
        try {
            List<Course> allCourses = getCourseDAO().getAll();
            for (Course course : allCourses) {
                if (isCourseMatching(course, filterInput)) {
                    beanList.add(convertCourseToBean(course));
                }
            }
        } catch (DAOException | UserNotFoundException e) {
            return new ArrayList<>();
        }
        return beanList;
    }

    public List<CourseBean> searchCourseByName(CourseBean bean) {
        if (bean == null || bean.getTitle() == null || bean.getTitle().isEmpty()) {
            return new ArrayList<>();
        }

        String searchString = bean.getCourseType() != null ? bean.getCourseType().trim().toLowerCase() : "";
        List<CourseBean> resultList = new ArrayList<>();

        try {
            List<Course> allCourses = getCourseDAO().getAll();
            for (Course course : allCourses) {
                if (course.getCourseTitle() == null) continue;
                if (course.getCourseTitle().toLowerCase().contains(searchString)) {
                    resultList.add(convertCourseToBean(course));
                }
            }
        } catch (DAOException | UserNotFoundException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public CourseBean findCourseInformation(CourseBean bean) {
        if (bean == null || bean.getTitle() == null ||
                bean.getUniversity() == null || bean.getUniversity().getName() == null) {
            return null;
        }

        String targetTitle = bean.getTitle();
        String targetUniName = bean.getUniversity().getName();

        try {
            List<Course> allCourses = getCourseDAO().getAll();
            for (Course course : allCourses) {
                if (isSameCourse(course, targetTitle, targetUniName)) {
                    return convertCourseToFullBean(course);
                }
            }
        } catch (DAOException | UserNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AdmissionRequirementBean findCourseAdmissionRequirements(CourseBean courseBean) throws DAOException {
        if (courseBean == null || courseBean.getTitle() == null) return null;
        String targetUniName = (courseBean.getUniversity() != null) ? courseBean.getUniversity().getName() : "";

        try {
            Course foundCourse = findCourseEntity(courseBean.getTitle(), targetUniName);

            AdmissionRequirementBean admissionBean = new AdmissionRequirementBean();
            admissionBean.setRequirements(extractRequirementsList(foundCourse));
            return admissionBean;

        } catch (DAOException | UserNotFoundException e) {
            throw new DAOException("Database error during requirements retrieval");
        }
    }

    // ==================================================================================
    // PUBLIC INTERFACE - USER ACTIONS (FAVORITES & APPLICATIONS)
    // ==================================================================================

    public void submitApplication(TokenBean tokenBean, ApplicationBean applicationBean)
            throws InvalidTokenException, DAOException, DataNotFoundException, InvalidCertificateException, UserNotFoundException {

        // 1. Validazione Studente
        Student student = validateSessionAndGetStudent(tokenBean);
        if (student == null) {
            throw new InvalidTokenException("User not authorized or not a student");
        }

        // 2. Recupero Corso
        if (applicationBean.getCourseBean() == null) {
            throw new DataNotFoundException("Course information missing");
        }
        String courseTitle = applicationBean.getCourseBean().getTitle();
        String uniName = (applicationBean.getCourseBean().getUniversity() != null)
                ? applicationBean.getCourseBean().getUniversity().getName() : null;

        Course courseEntity = findCourseEntity(courseTitle, uniName);

        // 3. Processamento Items
        List<ApplicationItem> validatedItems = processApplicationItems(courseEntity, applicationBean);

        Application newApplication = new Application(
                ApplicationStatus.DRAFT,
                LocalDateTime.now(),
                LocalDateTime.now(),
                student,
                courseEntity,
                validatedItems
        );

        newApplication.submit();

        try {
            ApplicationDAO appDAO = DAOFactory.getDAOFactory().getApplicationDAO();
            appDAO.insert(newApplication);

        } catch (DAOException e) {
            throw new DAOException("Error during application submission persistence: " + e.getMessage());
        }

        newApplication.finalizeApplication(tokenBean.getToken());

        LOGGER.info("Application submitted. Notifications persisted.");
    }

    public void evaluateApplication(TokenBean tokenBean, ApplicationBean appBean)
            throws InvalidTokenException, DAOException, DataNotFoundException, UserNotFoundException {

        if (!SessionManager.getInstance().sessionIsValid(tokenBean.getToken())) {
            throw new InvalidTokenException("Invalid token");
        }

        Application application = findApplicationInDAO(appBean);

        if (application == null) {
            throw new DataNotFoundException("Application not found. Please check data integrity.");
        }

        ApplicationEvaluation eval = appBean.getStatus().equals("ACCEPTED")  ? ApplicationEvaluation.ACCEPTED : ApplicationEvaluation.REJECTED;

        application.evaluateApplication(eval, tokenBean.getToken());

        try {
            ApplicationDAO appDAO = DAOFactory.getDAOFactory().getApplicationDAO();
            appDAO.update(application);

        } catch (DAOException e) {
            throw new DAOException("Error saving evaluation: " + e.getMessage());
        }



        LOGGER.log(Level.INFO,"Evaluation completed: {0}", eval);
    }

    // ==================================================================================
    // PRIVATE HELPERS - VALIDATION & PROCESSING
    // ==================================================================================

    private List<ApplicationItem> processApplicationItems(Course course, ApplicationBean applicationBean)
            throws InvalidCertificateException {

        List<ApplicationItem> validatedItems = new ArrayList<>();
        List<AbstractRequirement> requirements = course.getRequirements();

        for (AbstractRequirement req : requirements) {
            ApplicationItemBean inputBean = findInputItemByRequirementName(applicationBean.getItems(), req.getName());

            if (req instanceof DocumentRequirement documentRequirement) {
                DocumentRequirement docReq =  documentRequirement;

                if (docReq.isCertificate()) {
                    validateExternalCertificate(inputBean, docReq);
                }
            }

            ApplicationItem itemModel = convertBeanToApplicationItemModel(inputBean, req);

            try {
                req.validate(itemModel);
            } catch (Exception e) {
                throw new InvalidCertificateException("Validation Error on '" + req.getLabel() + "': " + e.getMessage());
            }

            validatedItems.add(itemModel);
        }
        return validatedItems;
    }

    private void validateExternalCertificate(ApplicationItemBean inputBean, DocumentRequirement docReq)
            throws InvalidCertificateException {

        if (inputBean == null || inputBean.getDocument() == null) {
            return;
        }

        CertificateValidationBoundary certBoundary = new CertificateValidationBoundary();

        CertificateValidationBean result = certBoundary.validateCertificate(inputBean.getDocument());

        if (!result.isValid()) {
            throw new InvalidCertificateException("External validation failed for certificate: " + docReq.getLabel());
        }

        LOGGER.log(Level.INFO,"External validation passed for: {0}", docReq.getLabel());
    }

    private ApplicationItemBean findInputItemByRequirementName(List<ApplicationItemBean> items, String reqName) {
        if (items == null) return null;
        for (ApplicationItemBean item : items) {
            if (item.getRequirementName() != null && item.getRequirementName().equals(reqName)) {
                return item;
            }
        }
        return null;
    }

    private ApplicationItem convertBeanToApplicationItemModel(ApplicationItemBean bean, AbstractRequirement req) {
        if (bean == null) {
            return new ApplicationItem(req.getName(), req.getRequirementType(), "", null);
        }

        if (req.getRequirementType() == RequirementType.TEXT) {
            String text = bean.getTextContent() != null ? bean.getTextContent() : "";
            return new ApplicationItem(req.getName(), text);
        }
        else if (req.getRequirementType() == RequirementType.DOCUMENT) {
            Document docEntity = convertBeanToDocumentEntity(bean.getDocument());
            return new ApplicationItem(req.getName(), docEntity);
        }

        return null;
    }

    private Document convertBeanToDocumentEntity(DocumentBean bean) {
        if (bean == null) return null;
        Document doc = new Document();
        doc.setFileName(bean.getFileName());
        doc.setFileType(bean.getFileType());
        doc.setFileSize(bean.getFileSize());
        doc.setContent(bean.getContent());
        return doc;
    }

    // ==================================================================================
    // PRIVATE HELPERS - SEARCH & EXTRACTION
    // ==================================================================================

    private Course findCourseEntity(String title, String uniName) throws DAOException, UserNotFoundException {
        List<Course> allCourses = getCourseDAO().getAll();
        for (Course course : allCourses) {
            if (isSameCourse(course, title, uniName)) {
                return course;
            }
        }
        throw new UserNotFoundException();
    }

    private List<RequirementBean> extractRequirementsList(Course course) {
        List<RequirementBean> requirementsList = new ArrayList<>();
        if (course.getAdmissionRequirements() == null ||
                course.getAdmissionRequirements().getRequirements() == null) {
            return requirementsList;
        }

        for (AbstractRequirement reqModel : course.getAdmissionRequirements().getRequirements()) {
            RequirementBean reqBean = convertModelToRequirementBean(reqModel);
            if (reqBean != null) {
                requirementsList.add(reqBean);
            }
        }
        return requirementsList;
    }




    private Application findApplicationInDAO(ApplicationBean appBean) throws DAOException, UserNotFoundException {
        ApplicationDAO appDAO = DAOFactory.getDAOFactory().getApplicationDAO();
        // Nota: Usare getAll() è inefficiente, ma per ora risolviamo il bug logico
        List<Application> allApplications = appDAO.getAll();

        for (Application app : allApplications) {

            String beanCourseTitle = appBean.getCourseBean() != null ? appBean.getCourseBean().getTitle() : null;
            String beanUniName = (appBean.getCourseBean() != null && appBean.getCourseBean().getUniversity() != null)
                    ? appBean.getCourseBean().getUniversity().getName() : null;
            String beanStudentUser = appBean.getStudentName() != null ? appBean.getStudentName().getUsername() : null;
            LocalDateTime creationDate = appBean.getCreationDate();

            boolean matchTitle = java.util.Objects.equals(app.getCourse().getCourseTitle(), beanCourseTitle);
            boolean matchUni = java.util.Objects.equals(app.getCourse().getUniversity().getName(), beanUniName);
            boolean matchStudent = java.util.Objects.equals(app.getApplicant().getUsername(), beanStudentUser);
            boolean matchCreationDate = java.util.Objects.equals(creationDate, appBean.getCreationDate());

            // RIMOSSO IL CONFRONTO ESATTO DELLA DATA
            // Spesso Studente + Corso + Università è sufficiente per identificare la domanda.

            // Opzionale: Se vuoi essere sicuro, controlla che lo stato non sia già finalizzato
            // o controlla l'ID se disponibile.
            if (matchTitle && matchUni && matchStudent && matchCreationDate) return app;
        }

        throw  new UserNotFoundException();
    }

    // ==================================================================================
    // PRIVATE HELPERS - DAO & MAPPERS
    // ==================================================================================

    private CourseDAO getCourseDAO() throws DAOException {
        return DAOFactory.getDAOFactory().getCourseDAO();
    }

    private boolean isSameCourse(Course course, String title, String uniName) {
        if (course == null || course.getUniversity() == null) return false;
        if (course.getCourseTitle() == null || title == null) return false;

        String currentUniName = course.getUniversity().getName();
        if (currentUniName == null && uniName != null) return false;
        if (currentUniName == null) return false;

        return course.getCourseTitle().equalsIgnoreCase(title) &&
                currentUniName.equalsIgnoreCase(uniName);
    }

    private FilterCourseBean mapCourseToFilterBean(Course course) {
        FilterCourseBean bean = new FilterCourseBean();
        bean.setLanguageOfInstruction(course.getLanguageOfInstruction());
        bean.setCourseDurationRange(calculateDurationRange(course.getCourseDuration()));

        if (course.getCourseType() != null) {
            bean.setCourseType(course.getCourseType().toString());
        }

        University uni = course.getUniversity();
        if (uni != null) {
            bean.setUniversityName(uni.getName());
            bean.setUniversityLocation(uni.findLocation());
            bean.setUniversityRankingRange(calculateRankingRange(uni.calculateRanking()));
        }
        return bean;
    }

    private RequirementBean convertModelToRequirementBean(AbstractRequirement reqModel) {
        if (reqModel == null) return null;

        RequirementBean bean = new RequirementBean();
        bean.setName(reqModel.getName());
        bean.setLabel(reqModel.getLabel());
        bean.setDescription(reqModel.getDescription());

        if (reqModel instanceof TextRequirement textRequirement) {
            TextRequirement textReq = textRequirement;
            bean.setType("TEXT");
            bean.setMinChars(textReq.getMinChars());
            bean.setMaxChars(textReq.getMaxChars());
        } else if (reqModel instanceof DocumentRequirement documentRequirement) {
            DocumentRequirement docReq = documentRequirement;
            bean.setType("DOCUMENT");
            bean.setMaxSizeMB(docReq.getMaxSizeMB());
            bean.setAllowedExtension(docReq.getAllowedExtension());
        }
        return bean;
    }

    private CourseBean convertCourseToBean(Course course) {
        if (course == null) return null;
        CourseBean bean = new CourseBean();
        bean.setTitle(course.getCourseTitle());
        bean.setDescription(course.getCourseDescription());
        bean.setDuration(course.getCourseDuration());
        bean.setFees(course.getCourseFees());
        bean.setLanguageOfInstruction(course.getLanguageOfInstruction());

        if (course.getCourseType() != null) {
            bean.setCourseType(course.getCourseType().toString());
        }
        if (course.getUniversity() != null) {
            UniversityBean uniBean = new UniversityBean();
            uniBean.setName(course.getUniversity().getName());
            bean.setUniversity(uniBean);
        }
        if (course.getTags() != null) {
            List<String> stringTags = new ArrayList<>();
            for (CourseTags tag : course.getTags()) {
                stringTags.add(tag.toString());
            }
            bean.setTags(stringTags);
        }
        return bean;
    }

    private CourseBean convertCourseToFullBean(Course course) {
        CourseBean bean = convertCourseToBean(course);
        if (bean != null && course.getUniversity() != null) {
            bean.setUniversity(convertUniversityToBean(course.getUniversity()));
        }
        return bean;
    }

    private UniversityBean convertUniversityToBean(University university) {
        if (university == null) return null;
        UniversityBean bean = new UniversityBean();
        bean.setName(university.getName());
        bean.setLivingCosts(university.getLivingCosts());
        bean.setRanking(university.getRanking());
        bean.setLocation(university.getLocation());
        bean.setContactInfo("Info");
        return bean;
    }

    // ==================================================================================
    // PRIVATE HELPERS - MATCHING LOGIC & CALCULATIONS
    // ==================================================================================

    private boolean isCourseMatching(Course course, FilterCourseBean filter) {
        if (filter == null) return true;
        return matchesUniversityName(course, filter.getUniversityName())
                && matchesLocation(course, filter.getUniversityLocation())
                && matchesRanking(course, filter.getUniversityRankingRange())
                && matchesLanguage(course, filter.getLanguageOfInstruction())
                && matchesCourseType(course, filter.getCourseType())
                && matchesDuration(course, filter.getCourseDurationRange());
    }

    private boolean matchesUniversityName(Course course, String filterName) {
        if (filterName == null || filterName.isEmpty()) return true;
        return course.getUniversity() != null && filterName.equals(course.getUniversity().getName());
    }

    private boolean matchesLocation(Course course, String filterLocation) {
        if (filterLocation == null || filterLocation.isEmpty()) return true;
        return course.getUniversity() != null && filterLocation.equals(course.getUniversity().findLocation());
    }

    private boolean matchesRanking(Course course, String filterRanking) {
        if (filterRanking == null || filterRanking.isEmpty()) return true;
        if (course.getUniversity() == null) return false;
        return filterRanking.equals(calculateRankingRange(course.getUniversity().calculateRanking()));
    }

    private boolean matchesLanguage(Course course, String filterLanguage) {
        if (filterLanguage == null || filterLanguage.isEmpty()) return true;
        return filterLanguage.equals(course.getLanguageOfInstruction());
    }

    private boolean matchesCourseType(Course course, String filterType) {
        if (filterType == null || filterType.isEmpty()) return true;
        String typeStr = (course.getCourseType() != null) ? course.getCourseType().toString() : null;
        return filterType.equals(typeStr);
    }

    private boolean matchesDuration(Course course, String filterDuration) {
        if (filterDuration == null || filterDuration.isEmpty()) return true;
        return filterDuration.equals(calculateDurationRange(course.getCourseDuration()));
    }

    private String calculateRankingRange(int rank) {
        if (rank <= 0) return "N/A";
        if (rank <= 100) return "1 - 100";
        if (rank <= 200) return "101 - 200";
        if (rank <= 500) return "201 - 500";
        return "500+";
    }

    private String calculateDurationRange(int duration) {
        if (duration <= 12) return "Short";
        if (duration <= 24) return "Medium";
        return "Long";
    }

    // ==================================================================================
    // PRIVATE HELPERS - SESSION & VALIDATION
    // ==================================================================================

    private Student validateSessionAndGetStudent(TokenBean tokenBean) throws InvalidTokenException {
        if (!SessionManager.getInstance().sessionIsValid(tokenBean.getToken())) {
            throw new InvalidTokenException("Invalid token");
        }
        Session session = SessionManager.getInstance().getSession(tokenBean.getToken());
        return session.isStudent() ? (Student) session.getUser() : null;
    }


}