package it.ispw.unilife.model;

import com.stripe.model.PaymentIntent;
import it.ispw.unilife.controller.NotificationSystem;
import it.ispw.unilife.enums.PaymentStatus;
import it.ispw.unilife.enums.ReservationStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.notification.Subject;

public class Reservation extends Subject {
    private Student student;
    private Lesson lesson;
    private Payment payment;
    private ReservationStatus status;

    public Reservation(Student student, Lesson lesson, Payment payment, ReservationStatus status) {
        this.student = student;
        this.lesson = lesson;
        this.payment = payment;
        this.status = status;
        attach(NotificationSystem.getInstance());
    }

    public void createReservation(String token) throws DAOException {
        this.status = ReservationStatus.PENDING;
        // NOTIFICA: Nuova prenotazione -> Notify Tutor
        System.out.println("NOTIFY RESERVATION CREATED");
        notifyObservers("CREATED", token);
    }

    // Quando il tutor accetta/rifiuta
    public void setStatus(ReservationStatus newStatus, String token) throws DAOException {
        this.status = newStatus;
        // NOTIFICA: Cambio stato -> Notify Student
        notifyObservers("UPDATED", token);
    }

    public void completeStatus( String token) throws DAOException {
        this.status = ReservationStatus.PAYED;
        notifyObservers("PAYED", token);
    }


    public void updatePayment(PaymentStatus paymentStatus, PaymentIntent paymentIntent){
        if (this.payment == null) {
            this.payment = new Payment(lesson.calculateTotalPrice(), paymentIntent, paymentStatus);
        } else {
            this.payment.updateStatus(paymentStatus);
            this.payment.setStripe(paymentIntent);
        }
    }

    public Student checkStudent() { return student; }
    public Lesson checkLesson(){ return lesson; }
    public Payment checkPayment(){ return payment; }
    public Tutor checkTutor(){ return lesson.getTutor(); }
    public ReservationStatus getStatus() { return this.status; }
}