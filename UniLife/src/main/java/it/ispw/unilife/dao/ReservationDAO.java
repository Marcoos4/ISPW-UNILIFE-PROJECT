package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Reservation;

import java.time.LocalDateTime;

public interface ReservationDAO extends DAO<Reservation> {
    Reservation getReservation(String studentUsername, String tutorUsername, LocalDateTime start) throws DAOException;
}
