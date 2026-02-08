package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.LessonBean;
import it.ispw.unilife.bean.ReservationBean;
import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.controller.BookTutor;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.view.viewcli.TutorBookingCLIView;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TutorBookingCLIController implements CLIContoller {
    private static final Logger LOGGER = Logger.getLogger(TutorBookingCLIController.class.getName());

    private final TutorBookingCLIView view = new TutorBookingCLIView();
    private final TokenBean tokenBean;
    private final ReservationBean currentReservation;
    private final LessonBean currentLesson;
    private final BookTutor bookTutorController = new BookTutor();

    public TutorBookingCLIController(TokenBean tokenBean, ReservationBean reservation) {
        this.tokenBean = tokenBean;
        this.currentReservation = reservation;
        this.currentLesson = reservation.getLesson();
    }

    @Override
    public void start(Scanner scanner) {
        view.showHeader();

        if (currentLesson != null) {
            view.showLessonDetails(currentLesson);
        }

        boolean flag = true;
        while (flag) {
            view.showMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    onConfirmBooking();
                    flag = false;
                    break;
                case "2":
                    view.showMessage("Not Implementing");
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

    private void onConfirmBooking() {
        try {
            if (currentLesson != null && tokenBean != null) {
                bookTutorController.confirmReservationProcedure(tokenBean, currentReservation);
            }
            view.showMessage("Booking confirmed successfully!");

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Booking error", e);
            view.showError("Submission Failed: " + e.getMessage());
        } catch (UserNotFoundException e) {
            view.showError("User not found : " + e.getMessage());
        }
    }
}
