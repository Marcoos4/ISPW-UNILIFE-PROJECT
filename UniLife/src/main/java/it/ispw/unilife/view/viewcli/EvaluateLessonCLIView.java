package it.ispw.unilife.view.viewcli;

import it.ispw.unilife.bean.LessonBean;

import java.time.format.DateTimeFormatter;

public class EvaluateLessonCLIView extends CLIView {
    public void showHeader() {
        System.out.println("\n=================================");
        System.out.println("   UNILIFE - EVALUATE LESSON      ");
        System.out.println("=================================");
    }

    public void showLessonDetails(LessonBean lesson) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.println("\nTutor: " + lesson.getTutor().getName());
        System.out.println("Subject: " + lesson.getSubject());
        if (lesson.getStartTime() != null) {
            System.out.println("Start: " + lesson.getStartTime().format(formatter));
        }
        if (lesson.getEndTime() != null) {
            System.out.println("End: " + lesson.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        System.out.println("Duration: " + lesson.getDurationInHours() + " Hours");
    }

    public void showMenu() {
        System.out.println("\nOptions:");
        System.out.println("1. Approve Lesson");
        System.out.println("2. Reject Lesson");
        System.out.println("3. Back");
        System.out.println("0. Quit");
        System.out.print("Choose: ");
    }
}
