package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.notification.CourseNotification;

import java.time.LocalDateTime;

public interface CourseNotificationDAO extends DAO<CourseNotification> {
    CourseNotification getCourseNotification(String username, LocalDateTime timestamp) throws DAOException;
}
