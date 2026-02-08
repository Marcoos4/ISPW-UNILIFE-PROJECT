package it.ispw.unilife.dao.factory;

import it.ispw.unilife.dao.*;
import it.ispw.unilife.dao.demo.*;

public class DemoDAOFactory extends DAOFactory {

    public DemoDAOFactory() {
        super();
    }

    @Override
    public UserDAO getUserDAO() {
        return DemoUserDAO.getInstance();
    }

    @Override
    public CourseDAO getCourseDAO() {
        return DemoCourseDAO.getInstance();
    }

    @Override
    public UniversityDAO getUniversityDAO() {
        return DemoUniversityDAO.getInstance();
    }

    @Override
    public ReservationDAO getReservationDAO() {
        return DemoReservationDAO.getInstance();
    }

    @Override
    public StudentDAO getStudentDAO() {
        return DemoStudentDAO.getInstance();
    }

    @Override
    public TutorDAO getTutorDAO() {
        return DemoTutorDAO.getInstance();
    }

    @Override
    public LessonDAO getLessonDAO() {
        return DemoLessonDAO.getInstance();
    }

    @Override
    public ApplicationDAO getApplicationDAO() {
        return DemoApplicationDAO.getInstance();
    }

    @Override
    public ReservationNotificationDAO getReservationNotificationDAO() {
        return DemoReservationNotificationDAO.getInstance();
    }

    @Override
    public NotificationDAO getNotificationDAO() {
        return DemoNotificationDAO.getInstance();
    }

    @Override
    public UniversityEmployeeDAO getUniversityEmployeeDAO() {
        return DemoUniversityEmployeeDAO.getInstance();
    }
}
