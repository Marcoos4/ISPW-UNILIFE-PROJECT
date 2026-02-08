package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.admission.Application;
import it.ispw.unilife.model.admission.ApplicationItem;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationItemDAO extends DAO<ApplicationItem> {
    List<ApplicationItem> getItems(String courseTitle, String universityName, String studentUsername, LocalDateTime creationDate) throws DAOException;
    void insert(ApplicationItem item, Application application) throws DAOException;
    void delete(ApplicationItem item, Application application) throws DAOException;
    void update(ApplicationItem item, Application application) throws DAOException;
}
