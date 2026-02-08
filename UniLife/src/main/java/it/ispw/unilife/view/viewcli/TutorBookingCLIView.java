package it.ispw.unilife.view.viewcli;

import it.ispw.unilife.bean.LessonBean;

import java.time.format.DateTimeFormatter;

public class TutorBookingCLIView extends CLIView {
    public void showHeader() {
        System.out.println("\n=================================");
        System.out.println("    UNILIFE - TUTOR BOOKING       ");
        System.out.println("=================================");
    }

    public void showLessonDetails(LessonBean lesson) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String date = lesson.getStartTime() != null ? lesson.getStartTime().format(dateFormatter) : "--";
        String time = lesson.getStartTime() != null ? lesson.getStartTime().format(timeFormatter) : "--";

        System.out.println("\n" + date + " | " + time + " | " + lesson.getDurationInHours() + " hours");
        System.out.println("Subject: " + (lesson.getSubject() != null ? lesson.getSubject() : "N/A"));

        if (lesson.getTutor() != null) {
            System.out.println("\n--- Tutor Info ---");
            System.out.println("Name: Prof. " + lesson.getTutor().getName() + " " + lesson.getTutor().getSurname());
            System.out.println("Rating: " + String.format("%.1f", lesson.getTutor().getRating()) + "/5");
        }

        System.out.println("\n--- Lesson Info ---");
        System.out.println("Mode: Online/In Person");
        System.out.println("Duration: " + lesson.getDurationInHours() + " hours");
        System.out.println("Price: " + lesson.getPrice() + " EUR/hour");
    }

    public void showMenu() {
        System.out.println("\nOptions:");
        System.out.println("1. Confirm Booking");
        System.out.println("2. Leave Review");
        System.out.println("3. Back to Search");
        System.out.println("0. Quit");
        System.out.print("Choose: ");
    }
}
