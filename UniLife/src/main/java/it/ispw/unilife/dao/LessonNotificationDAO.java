package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.notification.LessonNotification;

import java.time.LocalDateTime;

public interface LessonNotificationDAO extends DAO<LessonNotification> {
    LessonNotification getLessonNotification(String username, LocalDateTime timestamp) throws DAOException;
}
