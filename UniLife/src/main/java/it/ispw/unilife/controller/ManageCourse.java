package it.ispw.unilife.controller;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.dao.CourseDAO;
import it.ispw.unilife.dao.factory.DAOFactory;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.enums.CourseType;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.DataNotFoundException;
import it.ispw.unilife.exception.InvalidTokenException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.University;
import it.ispw.unilife.model.UniversityEmployee;
import it.ispw.unilife.model.User;
import it.ispw.unilife.model.admission.AbstractRequirement;
import it.ispw.unilife.model.admission.DocumentRequirement;
import it.ispw.unilife.model.admission.TextRequirement;
import it.ispw.unilife.model.session.SessionManager;

import java.util.logging.Logger;

public class ManageCourse {

    private static final Logger LOGGER = Logger.getLogger(ManageCourse.class.getName());


    public void addCourse(TokenBean tokenBean, CourseBean courseBean) throws InvalidTokenException, DAOException, DataNotFoundException {

        // 1. Validazione e Recupero Utente (Estrazione logica Auth)
        UniversityEmployee employee = getAuthorizedEmployee(tokenBean);

        // 2. Creazione Entità Base (Mapping semplice)
        Course newCourse = initBaseCourse(courseBean, employee.getUniversity());

        // 3. Gestione Enums (Type e Tags) - Gestione try-catch incapsulata
        enrichCourseWithEnums(newCourse, courseBean);

        // 4. Gestione Requisiti (Loop complesso estratto)
        processAdmissionRequirements(newCourse, courseBean);

        // 5. Persistenza
        CourseDAO courseDAO = DAOFactory.getDAOFactory().getCourseDAO();
        courseDAO.insert(newCourse);
        LOGGER.info("New course added successfully");
    }

    // --- METODI HELPER (LOGICA ESTRATTA) ---

    /**
     * Gestisce la validazione della sessione e del ruolo.
     */
    private UniversityEmployee getAuthorizedEmployee(TokenBean tokenBean) throws InvalidTokenException, DataNotFoundException {
        if (!SessionManager.getInstance().sessionIsValid(tokenBean.getToken())) {
            throw new InvalidTokenException("Invalid session");
        }

        User user = SessionManager.getInstance().getSession(tokenBean.getToken()).getUser();
        if (!(user instanceof UniversityEmployee)) {
            throw new InvalidTokenException("User is not authorized to create courses");
        }

        UniversityEmployee employee = (UniversityEmployee) user;
        if (employee.getUniversity() == null) {
            throw new DataNotFoundException("Employee is not associated with any university");
        }
        return employee;
    }

    /**
     * Inizializza l'oggetto Course con i campi base.
     */
    private Course initBaseCourse(CourseBean bean, University university) {
        Course course = new Course();
        course.setCourseTitle(bean.getTitle());
        course.setCourseDescription(bean.getDescription());
        course.setCourseDuration(bean.getDuration());
        course.setLanguageOfInstruction(bean.getLanguageOfInstruction());
        course.setCourseFees(bean.getFees());
        course.setUniversity(university);
        return course;
    }

    /**
     * Gestisce il parsing di CourseType e Tags.
     */
    private void enrichCourseWithEnums(Course course, CourseBean bean) throws InvalidTokenException {
        // Gestione Type
        if (bean.getCourseType() != null) {
            try {
                course.setCourseType(CourseType.valueOf(bean.getCourseType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidTokenException("Invalid course type: " + bean.getCourseType());
            }
        }

        // Gestione Tags
        if (bean.getTags() != null) {
            for (String tagName : bean.getTags()) {
                try {
                    course.addTag(CourseTags.valueOf(tagName));
                } catch (IllegalArgumentException e) {
                    // Ignora silenziosamente tag non validi (o loggali se preferisci)
                }
            }
        }
    }

    /**
     * Itera sui requisiti e li aggiunge al corso.
     */
    private void processAdmissionRequirements(Course course, CourseBean bean) {
        if (bean.getAdmissionRequirement() == null || bean.getAdmissionRequirement().getRequirements() == null) {
            return;
        }

        for (RequirementBean reqBean : bean.getAdmissionRequirement().getRequirements()) {
            AbstractRequirement reqModel = createRequirementModel(reqBean);
            if (reqModel != null) {
                course.addRequirement(reqModel);
            }
        }
    }

    /**
     * Factory Method: Crea l'istanza corretta (Text o Document) in base al tipo.
     * Riduce la complessità ciclotica all'interno del loop.
     */
    private AbstractRequirement createRequirementModel(RequirementBean reqBean) {
        // Normalizzazione stringhe comuni
        String safeName = toUpperSafe(reqBean.getName());
        String safeLabel = toUpperSafe(reqBean.getLabel());
        String safeDesc = toUpperSafe(reqBean.getDescription());

        if ("TEXT".equalsIgnoreCase(reqBean.getType())) {
            return new TextRequirement(
                    safeName, safeLabel, safeDesc,
                    reqBean.getMinChars(), reqBean.getMaxChars()
            );
        } else if ("DOCUMENT".equalsIgnoreCase(reqBean.getType())) {
            return new DocumentRequirement(
                    safeName, safeLabel, safeDesc,
                    toUpperSafe(reqBean.getAllowedExtension()),
                    (long) reqBean.getMaxSizeMB(),
                    reqBean.isCertificate()
            );
        }
        return null;
    }

    /**
     * Utility per evitare NullPointerException sulle stringhe.
     */
    private String toUpperSafe(String input) {
        return (input != null) ? input.toUpperCase() : "";
    }


}

