package it.ispw.unilife.dao.factory;

import it.ispw.unilife.dao.*;
import it.ispw.unilife.dao.db.*;
import it.ispw.unilife.exception.DAOException;

public class DBDAOFactory extends DAOFactory {

    public DBDAOFactory() {
        super();
    }

    @Override
    public UserDAO getUserDAO() throws DAOException {
        return DBUserDAO.getInstance();
    }

    @Override
    public CourseDAO getCourseDAO() throws DAOException {
        return DBCourseDAO.getInstance();
    }

    @Override
    public UniversityDAO getUniversityDAO() throws DAOException {
        return DBUniversityDAO.getInstance();
    }

    @Override
    public ReservationDAO getReservationDAO() throws DAOException {
        return DBReservationDAO.getInstance();
    }

    @Override
    public StudentDAO getStudentDAO() throws DAOException {
        return DBStudentDAO.getInstance();
    }

    @Override
    public TutorDAO getTutorDAO() throws DAOException {
        return DBTutorDAO.getInstance();
    }

    @Override
    public LessonDAO getLessonDAO() throws DAOException {return DBLessonDAO.getInstance();}

    @Override
    public ApplicationDAO getApplicationDAO() {return DBApplicationDAO.getInstance();}
    @Override
    public ReservationNotificationDAO getReservationNotificationDAO() throws DAOException {return DBReservationNotificationDAO.getInstance();}

    @Override
    public NotificationDAO getNotificationDAO() {return DBNotificationDAO.getInstance();}

    @Override
    public UniversityEmployeeDAO getUniversityEmployeeDAO() throws DAOException {
        return DBUniversityEmployeeDAO.getInstance();
    }
}
