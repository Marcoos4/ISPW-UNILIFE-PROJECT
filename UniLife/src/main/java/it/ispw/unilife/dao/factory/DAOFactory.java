package it.ispw.unilife.dao.factory;

import it.ispw.unilife.config.Configuration;
import it.ispw.unilife.config.PersistencyMode;
import it.ispw.unilife.dao.*;
import it.ispw.unilife.exception.DAOException;

public abstract class DAOFactory {

    private static DAOFactory instance = null;

    protected DAOFactory(){}

    public static synchronized DAOFactory getDAOFactory(){

        if(instance == null){
            Configuration config = Configuration.getInstance();
            PersistencyMode mode = config.getPersistencyMode();
            if(mode.equals(PersistencyMode.JDBC))
                instance = new DBDAOFactory();
            else if(mode.equals(PersistencyMode.DEMO))
                instance = new DemoDAOFactory();
            else
                instance = new JSONDAOFactory();
        }
        return instance;
    }

    public abstract UserDAO getUserDAO() throws DAOException;
    public abstract CourseDAO getCourseDAO() throws DAOException;
    public abstract UniversityDAO getUniversityDAO() throws DAOException;
    public abstract ReservationDAO getReservationDAO() throws DAOException;
    public abstract StudentDAO getStudentDAO() throws DAOException;
    public abstract TutorDAO getTutorDAO() throws DAOException;
    public abstract LessonDAO getLessonDAO() throws DAOException;
    public abstract ApplicationDAO getApplicationDAO();
    public abstract ReservationNotificationDAO getReservationNotificationDAO() throws DAOException;
    public abstract NotificationDAO getNotificationDAO();
    public abstract UniversityEmployeeDAO getUniversityEmployeeDAO() throws DAOException;
}
