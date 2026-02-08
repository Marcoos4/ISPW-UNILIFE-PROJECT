package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.controller.LoginController;
import it.ispw.unilife.controller.NotificationSystem;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.view.viewcli.ProfileCLIView;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileCLIController implements CLIContoller {
    private static final Logger logger = Logger.getLogger(ProfileCLIController.class.getName());

    private final ProfileCLIView view = new ProfileCLIView();
    private final TokenBean tokenBean;
    private final NotificationSystem notificationService = NotificationSystem.getInstance();
    private final LoginController loginController = new LoginController();
    private List<NotificationBean> notifications;
    private String currentUserRole;

    public ProfileCLIController(TokenBean tokenBean) {
        this.tokenBean = tokenBean;
    }

    @Override
    public void start(Scanner scanner) {
        view.showHeader();

        // Load user profile and role ONCE
        UserBean user = loginController.getProfile(tokenBean);
        currentUserRole = loginController.findUserRole(tokenBean).getRole();

        if (user != null) {
            view.showUserInfo(user, currentUserRole);
        }

        // Load notifications
        try {
            notifications = notificationService.getNotifications(tokenBean);
            view.showNotifications(notifications);
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error loading notifications", e);
            view.showError("Error loading notifications.");
        }

        boolean flag = true;
        while (flag) {
            view.showMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    openNotification(scanner);
                    break;
                case "2":
                    flag = false;
                    break;
                case "0":
                    System.exit(0);
                    break;
                default:
                    view.showError("Invalid input");
            }
        }
    }

    private void openNotification(Scanner scanner) {
        if (notifications == null || notifications.isEmpty()) {
            view.showError("No notifications to open.");
            return;
        }

        view.promptNotificationNumber();
        try {
            int num = Integer.parseInt(scanner.nextLine().trim());
            if (num < 1 || num > notifications.size()) {
                view.showError("Invalid notification number.");
                return;
            }

            NotificationBean notif = notifications.get(num - 1);

            // Check if notification can be opened
            if (canOpenNotification(notif)) {
                onOpenNotification(notif, scanner);
            } else {
                view.showMessage("This notification cannot be opened.");
                // Return to profile page (restart the menu loop)
            }

        } catch (NumberFormatException e) {
            view.showError("Invalid input.");
        }
    }

    /**
     * Determines if a notification can be opened based on type, role, and status.
     * Mirrors the logic from ProfileFXController.shouldShowOpenButton()
     */
    private boolean canOpenNotification(NotificationBean notif) {
        String type = notif.getNotificationType();

        // Students cannot open APPLICATION notifications
        if ("APPLICATION".equalsIgnoreCase(type) && "Student".equalsIgnoreCase(currentUserRole)) {
            return false;
        }

        // Tutors cannot open LESSON notifications
        if ("LESSON".equalsIgnoreCase(type) && "Tutor".equalsIgnoreCase(currentUserRole)) {
            return false;
        }

        // Check if RESERVATION is cancelled/rejected for Students
        if ("RESERVATION".equalsIgnoreCase(type) && "Student".equalsIgnoreCase(currentUserRole)) {
            return !isReservationCancelledOrRejected(notif);
        }

        return true;
    }

    /**
     * Checks if a reservation notification has been cancelled or rejected.
     * Mirrors the logic from ProfileFXController.checkAndUpdateReservationStatus()
     */
    private boolean isReservationCancelledOrRejected(NotificationBean notif) {
        try {
            ReservationBean resBean = notificationService.resolveReservationNotification(notif, tokenBean);

            if (resBean != null) {
                String realStatus = resBean.getStatus();
                return "CANCELLED".equalsIgnoreCase(realStatus) || "REJECTED".equalsIgnoreCase(realStatus);
            }
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error checking reservation status", e);
        }
        return false;
    }

    private void onOpenNotification(NotificationBean notif, Scanner scanner) {
        try {
            switch (notif.getNotificationType()) {
                case "RESERVATION":
                    handleReservationNotification(notif, scanner);
                    break;

                case "APPLICATION":
                    handleApplicationNotification(notif, scanner);
                    break;

                case "LESSON":
                    handleLessonNotification(notif, scanner);
                    break;

                case "COURSE":
                    view.showMessage("Course notification opened.");
                    break;

                default:
                    view.showMessage("Unknown notification type: " + notif.getNotificationType());
                    break;
            }

            logger.info("Notification opened: " + notif.getMessage());

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error opening notification", e);
            view.showError("Error opening notification.");
        }
    }

    private void handleReservationNotification(NotificationBean notif, Scanner scanner) throws DAOException {
        ReservationBean reservationBean = notificationService.resolveReservationNotification(notif, tokenBean);

        switch (reservationBean.getStatus()) {
            case "Pending":
                new LessonRequestCLIController(tokenBean, notif).start(scanner);
                break;
            case "Confirmed":
                new PaymentCLIController(tokenBean, notif).start(scanner);
                break;
            case "CANCELLED", "REJECTED":
                view.showMessage("Reservation status: " + reservationBean.getStatus());
                view.showMessage("This reservation cannot be opened.");
                break;
            default:
                view.showMessage("Reservation status: " + reservationBean.getStatus());
                break;
        }
    }

    private void handleApplicationNotification(NotificationBean notif, Scanner scanner) throws DAOException {
        ApplicationBean appBean = notificationService.resolveApplicationNotification(notif, tokenBean);

        if (appBean != null) {
            if ("UNIVERSITY_EMPLOYEE".equalsIgnoreCase(currentUserRole) || "University_employee".equalsIgnoreCase(currentUserRole)) {
                new EvaluateApplicationCLIController(tokenBean, appBean).start(scanner);
            } else {
                // Student viewing their own application
                view.showMessage("Application Status: " + appBean.getStatus());
                view.showMessage("This notification is for information only and cannot be opened.");
            }
        } else {
            view.showError("Unable to retrieve application details.");
        }
    }

    private void handleLessonNotification(NotificationBean notif, Scanner scanner) {
        if ("Tutor".equalsIgnoreCase(currentUserRole)) {
            view.showMessage("This notification cannot be opened by tutors.");
        } else {
            new EvaluateLessonCLIController(tokenBean, notif).start(scanner);
        }
    }
}