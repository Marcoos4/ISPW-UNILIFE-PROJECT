package it.ispw.unilife.model.notification;

import it.ispw.unilife.enums.NotificationStatus;
import it.ispw.unilife.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public abstract class Notification {
    private final User sender;
    private final String message;
    private final LocalDateTime timestamp;
    private NotificationStatus status;

    /**
     * Costruttore per NUOVE notifiche.
     * Imposta automaticamente: timestamp = ADESSO, status = PENDING.
     */
    protected Notification(String message, User user) {
        this.sender = user;
        this.message = message;
        // Tronchiamo ai secondi per evitare problemi di confronto con il DB MySQL
        this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        this.status = NotificationStatus.PENDING;
    }

    /**
     * Costruttore per il CARICAMENTO DA DB.
     * Accetta timestamp e status esistenti.
     */
    protected Notification(String message, User user, LocalDateTime timestamp, NotificationStatus status) {
        this.sender = user;
        this.message = message;
        this.timestamp = timestamp.truncatedTo(ChronoUnit.SECONDS);
        this.status = status;
    }

    public User getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void updateStatus(NotificationStatus status) {
        this.status = status;
    }
}