package it.ispw.unilife.view.viewcli;

import java.util.List;

public class RegisterCLIView extends CLIView {
    public void showOptions() {
        System.out.println("\n=================================");
        System.out.println("      UNILIFE - REGISTER          ");
        System.out.println("=================================");
    }

    public void promptUsername() { System.out.print("Insert Username: "); }
    public void promptPassword() { System.out.print("Insert Password: "); }
    public void promptName() { System.out.print("Insert Name: "); }
    public void promptSurname() { System.out.print("Insert Surname: "); }

    public void showRoleMenu() {
        System.out.println("\nSelect Role:");
        System.out.println("1. Student");
        System.out.println("2. Tutor");
        System.out.println("3. University Employee");
        System.out.print("Choose: ");
    }

    public void showUniversityMenu(List<String> universities) {
        System.out.println("\nSelect University:");
        for (int i = 0; i < universities.size(); i++) {
            System.out.println((i + 1) + ". " + universities.get(i));
        }
        System.out.print("Choose: ");
    }

    public void showSuccess() {
        showMessage("Registration Successful! Redirecting...");
    }
}
