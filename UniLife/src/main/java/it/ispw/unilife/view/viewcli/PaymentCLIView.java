package it.ispw.unilife.view.viewcli;

import it.ispw.unilife.bean.LessonBean;
import it.ispw.unilife.bean.ReservationBean;

import java.time.format.DateTimeFormatter;

public class PaymentCLIView extends CLIView {
    public void showHeader() {
        System.out.println("\n=================================");
        System.out.println("      UNILIFE - PAYMENT           ");
        System.out.println("=================================");
    }

    public void showPaymentDetails(ReservationBean reservation) {
        LessonBean lesson = reservation.getLesson();
        if (lesson == null) return;

        System.out.println("\n--- Lesson Summary ---");
        if (lesson.getStartTime() != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            System.out.println("Date: " + lesson.getStartTime().format(dateFormatter));
            System.out.println("Time: " + lesson.getStartTime().format(timeFormatter));
        }
        System.out.println("Subject: " + (lesson.getSubject() != null ? lesson.getSubject() : "N/A"));
        System.out.println("Duration: " + lesson.getDurationInHours() + " hours");
        if (lesson.getTutor() != null) {
            System.out.println("Tutor: " + lesson.getTutor().getName() + " " + lesson.getTutor().getSurname());
        }
        System.out.println("Price: " + String.format("%.2f EUR", lesson.getPrice()));
    }

    public void promptCardNumber() { System.out.print("Card Number: "); }
    public void promptCVV() { System.out.print("CVV: "); }
    public void promptHolder() { System.out.print("Card Holder: "); }

    public void showMenu() {
        System.out.println("\nOptions:");
        System.out.println("1. Process Payment");
        System.out.println("2. Cancel");
        System.out.print("Choose: ");
    }
}
