package it.ispw.unilife.view.viewcli;

import it.ispw.unilife.bean.LessonBean;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SearchTutorCLIView extends CLIView {
    public void showHeader() {
        System.out.println("\n=================================");
        System.out.println("    UNILIFE - SEARCH TUTOR        ");
        System.out.println("=================================");
    }

    public void showMenu() {
        System.out.println("\nOptions:");
        System.out.println("1. Apply Filters");
        System.out.println("2. Select a Lesson");
        System.out.println("3. Back to Home");
        System.out.println("0. Quit");
        System.out.print("Choose: ");
    }

    public void showSubjectMenu(List<String> subjects) {
        System.out.println("\nAvailable Subjects:");
        for (int i = 0; i < subjects.size(); i++) {
            System.out.println((i + 1) + ". " + subjects.get(i));
        }
        System.out.print("Choose (0 to skip): ");
    }

    public void promptDate() { System.out.print("Enter date (dd/MM/yyyy, or press Enter to skip): "); }
    public void promptMaxPrice() { System.out.print("Enter max price (or press Enter to skip): "); }

    public void showLessonList(List<LessonBean> lessons) {
        System.out.println("\n--- Tutor Available ---");
        if (lessons == null || lessons.isEmpty()) {
            System.out.println("No lessons match your filters.");
            return;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (int i = 0; i < lessons.size(); i++) {
            LessonBean l = lessons.get(i);
            String time = l.getStartTime() != null ? l.getStartTime().format(df) : "--";
            String subject = l.getSubject() != null ? l.getSubject() : "N/A";
            String tutorName = (l.getTutor() != null) ?
                    "Prof. " + l.getTutor().getName() + " " + l.getTutor().getSurname() : "Tutor";
            String rating = (l.getTutor() != null) ? String.format("%.1f", l.getTutor().getRating()) : "-";
            System.out.println((i + 1) + ". " + time + " | " + subject);
            System.out.println("   " + tutorName + " | €" + l.getPrice() + " | Rating: " + rating);
        }
    }

    public void promptLessonNumber() { System.out.print("Enter lesson number: "); }
}
