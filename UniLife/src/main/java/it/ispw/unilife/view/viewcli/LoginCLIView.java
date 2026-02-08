package it.ispw.unilife.view.viewcli;

public class LoginCLIView extends CLIView {
    public void showOptions() {
        System.out.println("\n=================================");
        System.out.println("      UNILIFE - LOGIN       ");
        System.out.println("=================================");
        System.out.println("1. Login with Credentials");
        System.out.println("2. Back to Home");
        System.out.println("0. Quit");
        System.out.print("Select action: ");
    }

    public void showSuccess() {
        showMessage("Login Successful! Redirecting...");
    }

    public void promptUsername() { System.out.print("Insert Username: "); }
    public void promptPassword() { System.out.print("Insert Password: "); }
}
