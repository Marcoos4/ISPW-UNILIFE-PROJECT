package it.ispw.unilife.dao.factory;

import it.ispw.unilife.dao.*;
import it.ispw.unilife.dao.json.*;
import it.ispw.unilife.exception.DAOException;

public class JSONDAOFactory extends DAOFactory {

    public JSONDAOFactory() {
        super();
    }

    @Override
    public UserDAO getUserDAO() throws DAOException {
        return JSONUserDAO.getInstance();
    }

    @Override
    public CourseDAO getCourseDAO() throws DAOException {
        return JSONCourseDAO.getInstance();
    }

    @Override
    public UniversityDAO getUniversityDAO() throws DAOException {
        return JSONUniversityDAO.getInstance();
    }

    @Override
    public ReservationDAO getReservationDAO() throws DAOException {
        return JSONReservationDAO.getInstance();
    }

    @Override
    public StudentDAO getStudentDAO() throws DAOException {
        return JSONStudentDAO.getInstance();
    }

    @Override
    public TutorDAO getTutorDAO() throws DAOException {
        return JSONTutorDAO.getInstance();
    }

    @Override
    public LessonDAO getLessonDAO() throws DAOException {
        return JSONLessonDAO.getInstance();
    }

    @Override
    public ApplicationDAO getApplicationDAO() {
        return JSONApplicationDAO.getInstance();
    }

    @Override
    public ReservationNotificationDAO getReservationNotificationDAO() throws DAOException {
        return JSONReservationNotificationDAO.getInstance();
    }

    @Override
    public NotificationDAO getNotificationDAO() {
        return JSONNotificationDAO.getInstance();
    }

    @Override
    public UniversityEmployeeDAO getUniversityEmployeeDAO() throws DAOException {
        return JSONUniversityEmployeeDAO.getInstance();
    }
}
