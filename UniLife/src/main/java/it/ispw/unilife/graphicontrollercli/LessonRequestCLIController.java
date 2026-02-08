package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.NotificationBean;
import it.ispw.unilife.bean.ReservationBean;
import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.controller.BookTutor;
import it.ispw.unilife.controller.NotificationSystem;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.view.viewcli.LessonRequestCLIView;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LessonRequestCLIController implements CLIContoller {
    private static final Logger LOGGER = Logger.getLogger(LessonRequestCLIController.class.getName());

    private final LessonRequestCLIView view = new LessonRequestCLIView();
    private final TokenBean tokenBean;
    private final NotificationBean currentNotification;
    private ReservationBean currentReservation;
    private final BookTutor bookTutorController = new BookTutor();
    private final NotificationSystem notificationService = NotificationSystem.getInstance();

    public LessonRequestCLIController(TokenBean tokenBean, NotificationBean notification) {
        this.tokenBean = tokenBean;
        this.currentNotification = notification;
    }

    @Override
    public void start(Scanner scanner) {
        view.showHeader();

        try {
            currentReservation = notificationService.resolveReservationNotification(currentNotification, tokenBean);
            if (currentReservation != null) {
                view.showRequestDetails(currentReservation);
            } else {
                view.showError("Unable to retrieve reservation details.");
                return;
            }
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error loading reservation", e);
            view.showError("Database error.");
            return;
        }

        boolean flag = true;
        while (flag) {
            view.showMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    onAccept();
                    flag = false;
                    break;
                case "2":
                    onReject();
                    flag = false;
                    break;
                case "3":
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

    private void onAccept() {
        try {
            if (currentReservation != null && tokenBean != null) {
                bookTutorController.acceptReservationProcedure(tokenBean, currentReservation);
            }
            view.showMessage("Reservation accepted.");
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error accepting reservation", e);
            view.showError("Error accepting reservation.");
        } catch (UserNotFoundException e) {
            view.showError("User not found.");
        }
    }

    private void onReject() {
        try {
            if (currentReservation != null && tokenBean != null) {
                bookTutorController.abortReservationProcedure(tokenBean, currentReservation);
            }
            view.showMessage("Reservation rejected.");
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error rejecting reservation", e);
            view.showError("Error rejecting reservation.");
        } catch (UserNotFoundException e) {
            view.showError("User not found.");
        }
    }
}
