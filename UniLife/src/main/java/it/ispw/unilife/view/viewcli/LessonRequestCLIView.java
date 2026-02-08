package it.ispw.unilife.view.viewcli;

import it.ispw.unilife.bean.ReservationBean;

import java.time.format.DateTimeFormatter;

public class LessonRequestCLIView extends CLIView {
    public void showHeader() {
        System.out.println("\n=================================");
        System.out.println("   UNILIFE - LESSON REQUEST       ");
        System.out.println("=================================");
    }

    public void showRequestDetails(ReservationBean reservation) {
        if (reservation.getStudent() != null) {
            System.out.println("\n--- Student Info ---");
            System.out.println("Name: " + reservation.getStudent().getName() + " " + reservation.getStudent().getSurname());
        }

        if (reservation.getLesson() != null) {
            System.out.println("\n--- Lesson Info ---");
            if (reservation.getLesson().getStartTime() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                System.out.println("Time: " + reservation.getLesson().getStartTime().format(formatter));
            }
            System.out.println("Subject: " + reservation.getLesson().getSubject());
            System.out.println("Duration: " + reservation.getLesson().getDurationInHours() + " hours");
            System.out.println("Price: " + reservation.getLesson().getPrice() + " EUR");
        }
    }

    public void showMenu() {
        System.out.println("\nOptions:");
        System.out.println("1. Accept");
        System.out.println("2. Reject");
        System.out.println("3. Back");
        System.out.println("0. Quit");
        System.out.print("Choose: ");
    }
}
