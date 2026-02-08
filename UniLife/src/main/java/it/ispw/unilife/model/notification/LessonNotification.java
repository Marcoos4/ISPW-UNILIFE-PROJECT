package it.ispw.unilife.model.notification;

import it.ispw.unilife.enums.NotificationStatus;
import it.ispw.unilife.model.Lesson;
import it.ispw.unilife.model.User;

import java.time.LocalDateTime;

public class LessonNotification extends Notification {

    private final Lesson lesson;

    /**
     * Costruttore per NUOVE notifiche (appena create).
     */
    public LessonNotification(String message, User user, Lesson lesson) {
        super(message, user);
        this.lesson = lesson;
    }

    /**
     * Costruttore per il CARICAMENTO DA DB (ricostruzione oggetto storico).
     */
    public LessonNotification(String message, User user, Lesson lesson, LocalDateTime timestamp, NotificationStatus status) {
        super(message, user, timestamp, status);
        this.lesson = lesson;
    }

    public Lesson getLesson() {
        return lesson;
    }
}