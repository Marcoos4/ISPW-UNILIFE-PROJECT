package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.admission.Application;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationDAO extends DAO<Application> {
    public List<Application> getApplications(String username) throws DAOException;
    Application getApplication(String courseTitle, String universityName, String studentUsername, LocalDateTime creationDate) throws DAOException;
}
