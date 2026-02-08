package it.ispw.unilife.view.viewcli;

public class StudentHomeCLIView extends CLIView {
    public void showMenu() {
        System.out.println("\n=================================");
        System.out.println("    UNILIFE - STUDENT HOME        ");
        System.out.println("=================================");
        System.out.println("1. Course Discovery");
        System.out.println("2. Book Tutor");
        System.out.println("3. Profile & Notifications");
        System.out.println("4. Logout");
        System.out.println("0. Quit");
        System.out.print("Choose an option: ");
    }
}
