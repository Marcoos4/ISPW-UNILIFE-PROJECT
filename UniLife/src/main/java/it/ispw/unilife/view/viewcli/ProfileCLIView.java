package it.ispw.unilife.view.viewcli;

import it.ispw.unilife.bean.NotificationBean;
import it.ispw.unilife.bean.UserBean;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProfileCLIView extends CLIView {
    public void showHeader() {
        System.out.println("\n=================================");
        System.out.println("      UNILIFE - PROFILE           ");
        System.out.println("=================================");
    }

    public void showUserInfo(UserBean user, String role) {
        System.out.println("\n--- User Info ---");
        System.out.println("Username: " + user.getUserName());
        System.out.println("Name: " + user.getName());
        System.out.println("Surname: " + user.getSurname());
        System.out.println("Role: " + role);
    }

    public void showNotifications(List<NotificationBean> notifications) {
        System.out.println("\n--- Notifications ---");
        if (notifications == null || notifications.isEmpty()) {
            System.out.println("No notifications.");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (int i = 0; i < notifications.size(); i++) {
            NotificationBean n = notifications.get(i);
            String time = n.getTimestamp() != null ? n.getTimestamp().format(formatter) : "--";
            System.out.println((i + 1) + ". [" + n.getNotificationType() + "] " + time);
            System.out.println("   From: " + n.getSenderUsername());
            System.out.println("   Message: " + n.getMessage());
            System.out.println("   Status: " + n.getStatus());
        }
    }

    public void showMenu() {
        System.out.println("\nOptions:");
        System.out.println("1. Open Notification");
        System.out.println("2. Back to Home");
        System.out.println("0. Quit");
        System.out.print("Choose: ");
    }

    public void promptNotificationNumber() {
        System.out.print("Enter notification number: ");
    }
}
