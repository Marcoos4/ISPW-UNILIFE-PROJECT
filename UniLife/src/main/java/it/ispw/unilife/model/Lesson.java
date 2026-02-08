package it.ispw.unilife.model;

import it.ispw.unilife.controller.NotificationSystem;
import it.ispw.unilife.enums.LessonStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.notification.Subject;

import java.time.Duration;
import java.time.LocalDateTime;

public class Lesson extends Subject {
    private final String subject;
    private float pricePerHour;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int durationInHours;
    private LessonStatus status;
    private Tutor tutor;

    public Lesson(String subject, LocalDateTime startTime, LocalDateTime endTime, float pricePerHour, Tutor tutor, LessonStatus status) {
        this.subject = subject;
        this.durationInHours = getDifferenceInHours(startTime, endTime);
        this.pricePerHour = pricePerHour;
        this.startTime = startTime;
        this.tutor = tutor;
        this.endTime = endTime;
        this.status = status;
        attach(NotificationSystem.getInstance());
    }

    public void createLesson(String token) throws DAOException {
        this.status = LessonStatus.PENDING;
        // NOTIFICA: Nuova prenotazione -> Notify Tutor
        notifyObservers("CREATED", token);
    }


    public void setStatus(LessonStatus status, String token) throws DAOException {
        this.status = status;
        notifyObservers("EVALUATED", token);
    }

    private int getDifferenceInHours(LocalDateTime start, LocalDateTime end) {
        return (int) Duration.between(start, end).toHours();
    }


    public float calculateTotalPrice() {
        return pricePerHour * durationInHours;
    }

    public String getSubject() {
        return subject;
    }
    public int getDurationInHours() {
        return durationInHours;
    }

    public float getPricePerHour() {
        return pricePerHour;
    }

    public void setDurationInHours(int durationInHours) {
        this.durationInHours = durationInHours;
    }

    public void setPricePerHour(float pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public boolean retrieveAvailability(LocalDateTime start, LocalDateTime end, String subject, float amount) {
        if (start == null || end == null) {
            return true;
        }

        return start.isBefore(getStartTime())
                && end.isAfter(getEndTime())
                && filterSubject(subject)
                && filterAmount(amount);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LessonStatus getStatus() {
        return status;
    }
    public void setStatus(LessonStatus status) {
        this.status = status;
    }

    private boolean filterSubject(String subject) {
        if (subject == null || subject.isEmpty()) {
            return true;
        }
        return subject.equals(getSubject());
    }

    private boolean filterAmount(float amount) {
        if (amount == 0) {
            return true;
        }
        return amount < getPricePerHour();
    }

}
