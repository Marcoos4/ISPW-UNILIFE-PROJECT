package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.NotificationBean;
import it.ispw.unilife.bean.PaymentBean;
import it.ispw.unilife.bean.ReservationBean;
import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.controller.BookTutor;
import it.ispw.unilife.controller.NotificationSystem;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.view.Navigator;
import it.ispw.unilife.view.viewcli.PaymentCLIView;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaymentCLIController implements CLIContoller {
    private static final Logger LOGGER = Logger.getLogger(PaymentCLIController.class.getName());

    private final PaymentCLIView view = new PaymentCLIView();
    private final TokenBean tokenBean;
    private final NotificationBean currentNotification;
    private ReservationBean currentReservation;
    private final BookTutor bookTutorController = new BookTutor();
    private final NotificationSystem notificationService = NotificationSystem.getInstance();

    public PaymentCLIController(TokenBean tokenBean, NotificationBean notification) {
        this.tokenBean = tokenBean;
        this.currentNotification = notification;
    }

    @Override
    public void start(Scanner scanner) {
        view.showHeader();

        try {
            currentReservation = notificationService.resolveReservationNotification(currentNotification, tokenBean);
            if (currentReservation != null) {
                view.showPaymentDetails(currentReservation);
            } else {
                view.showError("Unable to retrieve reservation details.");
                return;
            }
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error loading payment details", e);
            view.showError("System error retrieving data.");
            return;
        }

        view.showMenu();
        String input = scanner.nextLine().trim();

        switch (input) {
            case "1":
                onProcessPayment(scanner);
                break;
            case "2":
                view.showMessage("Payment cancelled.");
                break;
            default:
                view.showError("Invalid input. Payment cancelled.");
        }
    }

    private void onProcessPayment(Scanner scanner) {
        view.promptCardNumber();
        String cardNumber = scanner.nextLine().trim();
        if (cardNumber.isEmpty()) { view.showError("Card required"); return; }

        view.promptCVV();
        String cvv = scanner.nextLine().trim();
        if (cvv.isEmpty()) { view.showError("CVV required"); return; }

        view.promptHolder();
        String holder = scanner.nextLine().trim();
        if (holder.isEmpty()) { view.showError("Holder required"); return; }

        try {
            if (currentReservation != null && tokenBean != null) {
                PaymentBean paymentBean = new PaymentBean();

                if (currentReservation.getLesson() != null) {
                    paymentBean.setAmount(currentReservation.getLesson().getPrice());
                    paymentBean.setCurrency("eur");
                }

                paymentBean.setPaymentMethodId("pm_card_visa");
                paymentBean.setStatus("PENDING");

                bookTutorController.processPayment(Navigator.getInstance().getCurrentToken(), paymentBean, currentReservation);


                view.showMessage("Payment processed successfully!");
            }

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Payment error", e);
            view.showError("Payment failed: " + e.getMessage());
        } catch (UserNotFoundException e) {
            view.showError("User not found.");
        }
    }
}
