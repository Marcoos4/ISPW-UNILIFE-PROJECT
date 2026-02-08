package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.notification.ApplicationNotification;

import java.time.LocalDateTime;

public interface ApplicationNotificationDAO extends DAO<ApplicationNotification> {
    ApplicationNotification getApplicationNotification(String username, LocalDateTime timestamp) throws DAOException, UserNotFoundException;

}
