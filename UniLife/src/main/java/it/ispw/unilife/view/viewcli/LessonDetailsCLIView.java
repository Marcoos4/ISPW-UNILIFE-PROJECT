package it.ispw.unilife.view.viewcli;

public class LessonDetailsCLIView extends CLIView {
    public void showHeader() {
        System.out.println("\n=================================");
        System.out.println("    UNILIFE - ADD NEW LESSON      ");
        System.out.println("=================================");
    }

    public void promptSubject() { System.out.print("Subject: "); }

    public void showDurationMenu() {
        System.out.print("Duration (hours, 1-9): ");
    }

    public void promptDate() { System.out.print("Date (dd/MM/yyyy): "); }

    public void showStartTimeMenu() {
        System.out.print("Start time (HH:mm, e.g. 09:00): ");
    }

    public void promptPrice() { System.out.print("Price per hour: "); }

    public void showMenu() {
        System.out.println("\nOptions:");
        System.out.println("1. Confirm & Add Lesson");
        System.out.println("2. Back");
        System.out.print("Choose: ");
    }
}
