package it.ispw.unilife.model.notification;

import it.ispw.unilife.enums.NotificationStatus;
import it.ispw.unilife.model.User;
import it.ispw.unilife.model.admission.Application;

import java.time.LocalDateTime;

public class ApplicationNotification extends Notification {

    private final Application application;

    /**
     * Costruttore 1: Per creare una NUOVA notifica da inviare
     * (Chiama il costruttore padre che mette timestamp=NOW e status=PENDING)
     */
    public ApplicationNotification(String message, User user, Application application) {
        super(message, user);
        this.application = application;
    }

    /**
     * Costruttore 2: Per RICARICARE la notifica dal Database
     * (Chiama il costruttore padre che accetta timestamp e status storici)
     */
    public ApplicationNotification(String message, User user, Application application, LocalDateTime timestamp, NotificationStatus status) {
        super(message, user, timestamp, status);
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }
}