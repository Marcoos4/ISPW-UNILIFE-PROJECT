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
    private List<NotificationBean> notifications;

    public ProfileCLIController(TokenBean tokenBean) {
        this.tokenBean = tokenBean;
    }

    @Override
    public void start(Scanner scanner) {
        view.showHeader();

        // Load user profile
        LoginController loginController = new LoginController();
        UserBean user = loginController.getProfile(tokenBean);
        String role = loginController.findUserRole(tokenBean).getRole();
        if (user != null) {
            view.showUserInfo(user, role);
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
            onOpenNotification(notif, scanner);

        } catch (NumberFormatException e) {
            view.showError("Invalid input.");
        }
    }

    private void onOpenNotification(NotificationBean notif, Scanner scanner) {
        try {
            switch (notif.getNotificationType()) {
                case "RESERVATION":
                    ReservationBean reservationBean = notificationService.resolveReservationNotification(notif, tokenBean);
                    switch (reservationBean.getStatus()) {
                        case "Pending":
                            new LessonRequestCLIController(tokenBean, notif).start(scanner);
                            break;
                        case "Confirmed":
                            new PaymentCLIController(tokenBean, notif).start(scanner);
                            break;
                        default:
                            view.showMessage("Reservation status: " + reservationBean.getStatus());
                            break;
                    }
                    break;

                case "APPLICATION":
                    ApplicationBean appBean = notificationService.resolveApplicationNotification(notif, tokenBean);
                    if (appBean != null) {
                        LoginController loginController = new LoginController();
                        String currentUserRole = loginController.findUserRole(tokenBean).getRole();

                        if ("UNIVERSITY_EMPLOYEE".equalsIgnoreCase(currentUserRole) || "University_employee".equalsIgnoreCase(currentUserRole)) {
                            new EvaluateApplicationCLIController(tokenBean, appBean).start(scanner);
                        } else {
                            view.showMessage("Application Status: " + appBean.getStatus());
                        }
                    } else {
                        view.showError("Unable to retrieve application details.");
                    }
                    break;

                case "LESSON":
                    new EvaluateLessonCLIController(tokenBean, notif).start(scanner);
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
}
