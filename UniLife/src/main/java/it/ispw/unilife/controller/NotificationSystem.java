package it.ispw.unilife.controller;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.dao.NotificationDAO;
import it.ispw.unilife.dao.UserDAO;
import it.ispw.unilife.dao.factory.DAOFactory;
import it.ispw.unilife.enums.NotificationStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.*;
import it.ispw.unilife.model.admission.Application;
import it.ispw.unilife.model.admission.ApplicationItem;
import it.ispw.unilife.model.notification.*;
import it.ispw.unilife.model.session.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationSystem implements Observer {

    private static final Logger LOGGER = Logger.getLogger(NotificationSystem.class.getName());

    private static NotificationSystem instance = null;
    private static NotificationDAO notificationDAO;
    private static UserDAO userDAO;

    static {
        try {
            userDAO = DAOFactory.getDAOFactory().getUserDAO();
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private final SessionManager manager = SessionManager.getInstance();

    public static NotificationSystem getInstance() {
        if (instance == null) instance = new NotificationSystem();

        notificationDAO = DAOFactory.getDAOFactory().getNotificationDAO();

        return instance;
    }

    // --------- FACADE

    public List<NotificationBean> getNotifications(TokenBean tokenBean) throws DAOException {
        User user = SessionManager.getInstance().getSession(tokenBean.getToken()).getUser();
        List<Notification> notifications = notificationDAO.getAll(user);

        List<Notification> pendingNotifications = new ArrayList<>();
        for (Notification notification : notifications) {
            if (notification.getStatus() == NotificationStatus.PENDING) {
                pendingNotifications.add(notification);
            }
        }
        return convertToBeanList(pendingNotifications);
    }

    public ReservationBean resolveReservationNotification(NotificationBean notificationBean, TokenBean tokenBean) throws DAOException {
        // Adattiamo la chiamata al nuovo metodo findNotificationModel che prende stringa
        Notification notification = findNotificationModel(tokenBean.getToken(), notificationBean);

        if (notification instanceof ReservationNotification resNotification) {
            Reservation reservation = resNotification.getReservation();

            BookTutor bookTutor = new BookTutor();
            return bookTutor.convertReservationToBean(reservation);
        }

        return null;
    }

    public LessonBean getLessonFromNotification(TokenBean tokenBean, NotificationBean notificationBean) throws DAOException {


        Lesson lesson = resolveLessonNotification(notificationBean, tokenBean.getToken());

        return convertModelToBean(lesson);
    }

    public Lesson resolveLessonNotification(NotificationBean notificationBean, String token) throws DAOException {
        Notification notification = findNotificationModel(token, notificationBean);

        if (notification instanceof LessonNotification lessonNotification) {
            return lessonNotification.getLesson();
        }

        return null;
    }

    public ApplicationBean resolveApplicationNotification(NotificationBean notificationBean, TokenBean tokenBean) throws DAOException {
        // Adattiamo la chiamata al nuovo metodo findNotificationModel che prende stringa
        Notification notification = findNotificationModel(tokenBean.getToken(), notificationBean);

        if (notification instanceof ApplicationNotification appNotification) {
            Application application = appNotification.getApplication();
            return convertApplicationToBean(application);
        }

        return null;
    }

    private ApplicationBean convertApplicationToBean(Application application) {
        if (application == null) return null;

        ApplicationBean bean = new ApplicationBean();

        // Il metodo principale è ora una semplice lista di operazioni
        mapBaseInfo(bean, application);
        mapStudent(bean, application);
        mapCourse(bean, application);
        mapApplicationItems(bean, application);

        return bean;
    }

    // --- METODI HELPER (LOGICA ESTRATTA) ---

    private void mapBaseInfo(ApplicationBean bean, Application application) {
        if (application.getSubmissionDate() != null) {
            bean.setSubmissionDate(application.getSubmissionDate().toString());
        } else if (application.getCreationDate() != null) {
            bean.setCreationDate(application.getCreationDate());
        }

        if (application.getStatus() != null) {
            bean.setStatus(application.getStatus().toString());
        }
    }

    private void mapStudent(ApplicationBean bean, Application application) {
        if (application.getApplicant() == null) return;

        StudentBean studentBean = new StudentBean();
        studentBean.setUsername(application.getApplicant().getUsername());
        studentBean.setName(application.getApplicant().getName());
        studentBean.setSurname(application.getApplicant().getSurname());

        bean.setStudentName(studentBean);
    }

    private void mapCourse(ApplicationBean bean, Application application) {
        if (application.getCourse() == null) return;

        CourseBean courseBean = new CourseBean();
        courseBean.setTitle(application.getCourse().getCourseTitle());

        if (application.getCourse().getUniversity() != null) {
            UniversityBean uniBean = new UniversityBean();
            uniBean.setName(application.getCourse().getUniversity().getName());
            courseBean.setUniversity(uniBean);
        }

        bean.setCourseBean(courseBean);
    }

    private void mapApplicationItems(ApplicationBean bean, Application application) {
        if (application.getItems() == null || application.getItems().isEmpty()) return;

        List<ApplicationItemBean> itemBeans = new ArrayList<>();

        for (ApplicationItem modelItem : application.getItems()) {
            itemBeans.add(convertSingleItem(modelItem));
        }

        bean.setItems(itemBeans);
    }

    /**
     * Converte un singolo item (riduce la complessità dentro il ciclo for).
     */
    private ApplicationItemBean convertSingleItem(ApplicationItem modelItem) {
        ApplicationItemBean itemBean = new ApplicationItemBean();

        itemBean.setRequirementName(modelItem.getRequirementName());
        itemBean.setTextContent(modelItem.getTextContent());

        if (modelItem.getType() != null) {
            itemBean.setType(modelItem.getType().toString());
        }

        // Mappatura Documento estratta per pulizia
        if (modelItem.getDocumentContent() != null) {
            itemBean.setDocument(convertDocument(modelItem));
        }

        return itemBean;
    }

    private DocumentBean convertDocument(ApplicationItem modelItem) {
        DocumentBean docBean = new DocumentBean();
        // Assumiamo che getDocumentContent() non sia null qui perché controllato dal chiamante
        var docContent = modelItem.getDocumentContent();

        docBean.setFileName(docContent.getFileName());
        docBean.setFileType(docContent.getFileType());
        docBean.setFileSize(docContent.getFileSize());
        docBean.setContent(docContent.getContent());

        return docBean;
    }

    private LessonBean convertModelToBean(Lesson lesson) {
        if (lesson == null) return null;

        LessonBean bean = new LessonBean();
        bean.setSubject(lesson.getSubject());
        bean.setStartTime(lesson.getStartTime());
        bean.setEndTime(lesson.getEndTime());
        bean.setDurationInHours(lesson.getDurationInHours());

        if (lesson.getTutor() != null) {
            bean.setTutor(convertToTutorBean(lesson.getTutor()));
        }

        return bean;
    }

    private TutorBean convertToTutorBean(Tutor tutor) {
        TutorBean tutorBean = new TutorBean();
        tutorBean.setName(tutor.getName());
        tutorBean.setSurname(tutor.getSurname());
        tutorBean.setRating(tutor.getRating());
        tutorBean.setUsername(tutor.getUsername());
        return tutorBean;
    }

    private Notification findNotificationModel(String token, NotificationBean bean) throws DAOException {
        User user = SessionManager.getInstance().getSession(token).getUser();
        List<Notification> notifications = notificationDAO.getAll(user);

        for (Notification notification : notifications) {
            if (matchesBean(notification, bean)) {
                return notification;
            }
        }
        return null;
    }

    private boolean matchesBean(Notification notification, NotificationBean bean) {
        return notification.getSender().getUsername().equals(bean.getSenderUsername())
                && notification.getTimestamp().equals(bean.getTimestamp());
    }


    private List<NotificationBean> convertToBeanList(List<Notification> notifications) {
        List<NotificationBean> beans = new ArrayList<>();
        for (Notification notification : notifications) {
            beans.add(convertToBean(notification));
        }
        return beans;
    }

    private NotificationBean convertToBean(Notification notification) {
        NotificationBean bean = new NotificationBean();
        bean.setSenderUsername(notification.getSender().getUsername());
        bean.setMessage(notification.getMessage());
        bean.setTimestamp(notification.getTimestamp());
        bean.setStatus(notification.getStatus().toString());
        bean.setNotificationType(resolveNotificationType(notification));
        return bean;
    }

    private String resolveNotificationType(Notification notification) {
        if (notification instanceof ReservationNotification) {
            return "RESERVATION";
        } else if (notification instanceof LessonNotification) {
            return "LESSON";
        } else if (notification instanceof CourseNotification) {
            return "COURSE";
        } else if (notification instanceof ApplicationNotification) {
            return "APPLICATION";
        }
        return "UNKNOWN";
    }

    // --------- OBSERVER

    @Override
    public void update(Subject subject, Object arg, String token) throws DAOException {

        String event = (String) arg;

        if (subject instanceof Application application) {
            handleApplicationEvent(application, event, token);
        } else if (subject instanceof Lesson lesson) {
            handleLessonEvent(lesson, event, token);
        } else if (subject instanceof Reservation reservation) {
            handleReservationEvent(reservation, event, token);
        }
    }

    private void handleApplicationEvent(Application app, String eventType, String token) throws DAOException {
        if ("SUBMITTED".equals(eventType)) {
            LOGGER.info("Application SUBMITTED");
            // 1. Application Creata -> Notifica tutti gli University Employee di quell'università
            List<User> employees = findEmployeesFromDAO(app.getCourse());

            for (User employee : employees) {
                Notification notif = new ApplicationNotification(
                        "New application for " + app.getCourse().getCourseTitle(),
                        app.getApplicant(),
                        app
                );

                try{
                    notificationDAO.insert(notif, employee);
                } catch (DAOException e) {
                    throw new DAOException(e.getMessage());
                }

            }

        } else if ("EVALUATED".equals(eventType)) {
            // 2. Application Valutata -> Notifica Studente
            Notification notif = new ApplicationNotification(
                    "Your application has been " + app.getStatus(),
                    manager.getSession(token).getUser(),
                    app
            );

            notificationDAO.insert(notif,app.getApplicant());

            updateApplicationNotificationStatus(app, token);
        }
    }

    private void handleLessonEvent(Lesson lesson, String eventType, String token) throws DAOException {
        if ("CREATED".equals(eventType)) {
            // 4. Lezione Creata -> Notifica TUTTI gli University Employee
            List<User> allEmployees = getAllEmployees();
            for (User employee : allEmployees) {
                // Nota: Dovresti creare la classe LessonNotification simile alle altre
                Notification notif = new LessonNotification(
                        "New lesson available: " + lesson.getSubject(),
                        lesson.getTutor(),
                        lesson
                );
                notificationDAO.insert(notif, employee);
            }

        } else if ("EVALUATED".equals(eventType)) {
            // 2. Lezione Valutata -> Notifica Tutor
            Notification notif = new LessonNotification(
                    "Your Lesson has been " + lesson.getStatus(),
                    manager.getSession(token).getUser(),
                    lesson
            );

            notificationDAO.insert(notif,lesson.getTutor());

            updateLessonNotificationStatus(lesson, token);
        }
    }

    private void handleReservationEvent(Reservation res, String eventType, String token) throws DAOException {
        if ("CREATED".equals(eventType)) {
            LOGGER.info("Reservation CREATED");
            // 5. Reservation Creata -> Notifica il Tutor
            Notification notif = new ReservationNotification(
                    "New reservation request from " + res.checkStudent().getUsername(),
                    res,
                    res.checkStudent()
            );
            notificationDAO.insert(notif, res.checkTutor());

        } else if ("UPDATED".equals(eventType)) {
            // 6. Reservation Accettata/Rifiutata -> Notifica lo Studente
            Notification notif = new ReservationNotification(
                    "Your reservation has been " + res.getStatus(),
                    res,
                    res.checkTutor()
            );
            notificationDAO.insert(notif, res.checkStudent());
            updateReservationNotificationStatus(res, token);
        } else if ("PAYED".equals(eventType)) {
            updateReservationNotificationStatus(res, token);
        }
    }


    // ------ HELPER METHODS

    private List<User> findEmployeesFromDAO(Course course) throws DAOException {

        List<User> result = new ArrayList<>();
        List<User> daoResult;

        try {
            daoResult = userDAO.getAll();
        } catch (DAOException | UserNotFoundException e){
            throw new DAOException("Cant't find all users from database" + e.getMessage());
        }

        for(User user: daoResult){
            if(user instanceof UniversityEmployee employee && (employee.getUniversity().equals(course.getUniversity()))){
                result.add(user);
            }
        }

        return result;
    }

    private List<User> getAllEmployees() throws DAOException {
        List<User> result = new ArrayList<>();
        List<User> daoResult;

        try {
            daoResult = userDAO.getAll();
        } catch (DAOException | UserNotFoundException e){
            throw new DAOException("Cant't find all users from database" + e.getMessage());
        }

        for(User user: daoResult){
            if(user instanceof UniversityEmployee){
                result.add(user);
            }
        }

        return result;
    }

    private void updateApplicationNotificationStatus(Application app, String token) throws DAOException {
        User user = manager.getSession(token).getUser();
        List<Notification> notifications;

        try{
            notifications = notificationDAO.getAll(user);
        } catch (DAOException e){
            throw new DAOException(e.getMessage());
        }

        for(Notification notification : notifications) {
            if (notification instanceof ApplicationNotification applicationNotification && (applicationNotification).getApplication().equals(app)) {
                notification.updateStatus(NotificationStatus.COMPLETED);

                notificationDAO.update(notification);
            }
        }
    }

    private void updateLessonNotificationStatus(Lesson lesson, String token) throws DAOException {
        User user = manager.getSession(token).getUser();
        List<Notification> notifications;

        try{
            notifications = notificationDAO.getAll(user);
        } catch (DAOException e){
            throw new DAOException(e.getMessage());
        }

        for(Notification notification : notifications) {
            if (notification instanceof LessonNotification lessonNotification && (lessonNotification).getLesson().equals(lesson)) {
                notification.updateStatus(NotificationStatus.COMPLETED);
                LOGGER.info("Lesson notification has been updated");
                notificationDAO.update(notification);
            }
        }
    }

    private void updateReservationNotificationStatus(Reservation res, String token) throws DAOException {
        User user = manager.getSession(token).getUser();
        List<Notification> notifications;

        try{
            notifications = notificationDAO.getAll(user);
        } catch (DAOException e){
            throw new DAOException(e.getMessage());
        }

        for(Notification notification : notifications) {
            if (notification instanceof ReservationNotification reservationNotification && (reservationNotification).getReservation().equals(res)) {
                LOGGER.info("application foudn");
                notification.updateStatus(NotificationStatus.COMPLETED);
                notificationDAO.update(notification);
            }
        }
    }

}
