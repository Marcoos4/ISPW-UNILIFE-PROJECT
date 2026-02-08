package it.ispw.unilife.model.admission;

import it.ispw.unilife.controller.NotificationSystem;
import it.ispw.unilife.dao.ApplicationDAO;
import it.ispw.unilife.enums.ApplicationEvaluation;;
import it.ispw.unilife.enums.ApplicationStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.Document;
import it.ispw.unilife.model.Student;
import it.ispw.unilife.model.User;
import it.ispw.unilife.model.notification.ApplicationNotification;
import it.ispw.unilife.model.notification.Subject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Application  extends Subject {
    private ApplicationStatus status;
    private LocalDateTime submissionDate;
    private LocalDateTime creationDate;
    private Student applicant;
    private Course course;

    private List<ApplicationItem> items;

    public Application(ApplicationStatus status, LocalDateTime submissionDate, LocalDateTime creationDate, Student applicant, Course course, List<ApplicationItem> items) {
        this.status = status;
        this.submissionDate = submissionDate.truncatedTo(ChronoUnit.SECONDS);
        this.creationDate = creationDate;
        this.applicant = applicant;
        this.course = course;
        this.items = items;
        attach(NotificationSystem.getInstance());
    }

    public void submit() {
        this.status = ApplicationStatus.SUBMITTED;
    }

    public void finalizeApplication(String token) throws DAOException {
        this.submissionDate = LocalDateTime.now();
        notifyObservers("SUBMITTED", token);
    }

    // Quando l'impiegato valuta
    public void evaluateApplication(ApplicationEvaluation evaluation, String token) throws DAOException {
        if (evaluation == ApplicationEvaluation.ACCEPTED) {
            this.status = ApplicationStatus.ACCEPTED;
        } else {
            this.status = ApplicationStatus.REJECTED;
        }
        // NOTIFICA: Application valutata -> Notify Student + Clean Employee notifs
        notifyObservers("EVALUATED", token);
    }

    public void attachItem(ApplicationItem item) {
        if (item != null) {
            this.items.add(item);
        }
    }

    public void removeItem(ApplicationItem item){
        this.items.remove(item);
    }



    public Student getApplicant() {
        return applicant;
    }

    public Course getCourse() {
        return course;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public List<ApplicationItem> getItems() {
        return items;
    }
}
