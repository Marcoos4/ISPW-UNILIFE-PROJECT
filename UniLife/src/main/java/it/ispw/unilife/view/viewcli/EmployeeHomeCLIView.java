package it.ispw.unilife.view.viewcli;

public class EmployeeHomeCLIView extends CLIView {
    public void showMenu() {
        System.out.println("\n=================================");
        System.out.println("    UNILIFE - EMPLOYEE HOME       ");
        System.out.println("=================================");
        System.out.println("1. Create Course");
        System.out.println("2. Profile & Notifications");
        System.out.println("3. Logout");
        System.out.println("0. Quit");
        System.out.print("Choose an option: ");
    }
}
