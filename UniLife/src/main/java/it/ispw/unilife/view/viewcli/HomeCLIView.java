package it.ispw.unilife.view.viewcli;

public class HomeCLIView extends CLIView {
    public void showGuestMenu() {
        System.out.println("\n=================================");
        System.out.println("      UNILIFE - GUEST HOME       ");
        System.out.println("=================================");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Quit");
        System.out.print("Choose an option: ");
    }
}
