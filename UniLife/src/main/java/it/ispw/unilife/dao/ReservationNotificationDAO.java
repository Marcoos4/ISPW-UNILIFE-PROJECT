package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.notification.ReservationNotification;

import java.time.LocalDateTime;

public interface ReservationNotificationDAO extends DAO<ReservationNotification> {
    ReservationNotification getReservationNotification(String senderUsername, LocalDateTime date) throws DAOException;
}
