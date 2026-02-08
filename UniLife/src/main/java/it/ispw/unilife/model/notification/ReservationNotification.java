package it.ispw.unilife.model.notification;

import it.ispw.unilife.enums.NotificationStatus;
import it.ispw.unilife.model.Reservation;
import it.ispw.unilife.model.User;

import java.time.LocalDateTime;

public class ReservationNotification extends Notification {

    private final Reservation reservation;

    public ReservationNotification(String message, Reservation reservation, User user) {
        super(message, user);
        this.reservation = reservation;
    }

    public ReservationNotification(String message, Reservation reservation, User user, LocalDateTime timestamp, NotificationStatus status) {
        super(message, user, timestamp, status);
        this.reservation = reservation;
    }

    public Reservation getReservation() {
        return reservation;
    }
}