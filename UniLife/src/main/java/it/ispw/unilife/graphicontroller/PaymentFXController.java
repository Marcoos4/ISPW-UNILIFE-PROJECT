package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.LessonBean;
import it.ispw.unilife.bean.NotificationBean;
import it.ispw.unilife.bean.PaymentBean;
import it.ispw.unilife.bean.ReservationBean;
import it.ispw.unilife.controller.BookTutor;
import it.ispw.unilife.controller.NotificationSystem;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaymentFXController {

    private static final Logger LOGGER = Logger.getLogger(PaymentFXController.class.getName());
    private static final String PROFILEFXML = "Profile.fxml";

    @FXML private Button btnLogout;
    @FXML private Button btnProfile;
    @FXML private Button btnHome;
    @FXML private TextField txtCardNumber;
    @FXML private TextField txtCVV;
    @FXML private TextField txtHolder;
    @FXML private Button btnProcessPayment;
    @FXML private Button btnCancel;
    @FXML private Label lblLessonTime;
    @FXML private Label lblLessonSubject;
    @FXML private Label lblPrice;
    @FXML private Label lblError;
    @FXML private VBox lessonSummaryContainer;

    private final BookTutor bookTutorController;

    private ReservationBean currentReservation;

    private NotificationSystem notificationSystem = NotificationSystem.getInstance();

    public PaymentFXController() {
        this.bookTutorController = new BookTutor();
    }

    @FXML
    public void initialize() {
        Object data = Navigator.getInstance().getCurrentData();

        try {
            if (data instanceof NotificationBean notificationBean) {
                NotificationBean currentNotification = notificationBean;
                currentReservation = notificationSystem.resolveReservationNotification(
                        currentNotification,
                        Navigator.getInstance().getCurrentToken()
                );

                if (currentReservation != null) {
                    populatePaymentDetails(currentReservation);
                } else {
                    showError("Impossibile recuperare i dettagli della prenotazione.");
                }
            } else if (data instanceof ReservationBean reservationBean) {
                currentReservation = reservationBean;
                populatePaymentDetails(currentReservation);
            }
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inizializzazione del pagamento", e);
            showError("Errore di sistema nel recupero dati.");
        }

        if (lblError != null) {
            lblError.setVisible(false);
        }
    }

    private void populatePaymentDetails(ReservationBean reservation) {
        LessonBean lesson = reservation.getLesson();
        if (lesson == null) return;

        if (lblLessonTime != null) {
            lblLessonTime.setText(formatLessonTime(lesson));
        }
        if (lblLessonSubject != null) {
            lblLessonSubject.setText(lesson.getSubject() != null ? lesson.getSubject() : "Lesson");
        }
        if (lblPrice != null) {
            float totalCost = lesson.getPrice();
            lblPrice.setText(String.format("Price: %.2f EUR", totalCost));
        }

        if (lessonSummaryContainer != null) {
            lessonSummaryContainer.getChildren().clear();
            lessonSummaryContainer.getChildren().add(new Label("Subject: " + (lesson.getSubject() != null ? lesson.getSubject() : "N/A")));
            lessonSummaryContainer.getChildren().add(new Label("Duration: " + lesson.getDurationInHours() + " hours"));
            if (lesson.getTutor() != null) {
                lessonSummaryContainer.getChildren().add(new Label("Tutor: " + lesson.getTutor().getName() + " " + lesson.getTutor().getSurname()));
            }
        }
    }

    private String formatLessonTime(LessonBean lesson) {
        if (lesson.getStartTime() == null) {
            return "-- | -- | --";
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String date = lesson.getStartTime().format(dateFormatter);
        String time = lesson.getStartTime().format(timeFormatter);
        int duration = lesson.getDurationInHours();
        return String.format("%s | %s | %d hours", date, time, duration);
    }

    @FXML
    public void onProcessPayment(ActionEvent event) {
        hideError();
        if (txtCardNumber.getText() == null || txtCardNumber.getText().isEmpty()) { showError("Card required"); return; }
        if (txtCVV.getText() == null || txtCVV.getText().isEmpty()) { showError("CVV required"); return; }
        if (txtHolder.getText() == null || txtHolder.getText().isEmpty()) { showError("Holder required"); return; }

        try {
            if (currentReservation != null && Navigator.getInstance().getCurrentToken() != null) {
                PaymentBean paymentBean = new PaymentBean();

                if (currentReservation.getLesson() != null) {
                    paymentBean.setAmount(currentReservation.getLesson().getPrice()*100);
                    paymentBean.setCurrency("eur");
                }

                paymentBean.setPaymentMethodId("pm_card_visa");
                paymentBean.setStatus("PENDING");

                bookTutorController.processPayment(Navigator.getInstance().getCurrentToken(), paymentBean, currentReservation);

                Navigator.getInstance().clearCurrentData();
                Navigator.getInstance().goTo(event, PROFILEFXML);
            }
        } catch (DAOException | UserNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il pagamento", e);
            showError("Pagamento fallito: " + e.getMessage());
        }
    }

    @FXML public void onCancel(ActionEvent event) { Navigator.getInstance().goTo(event, PROFILEFXML); }
    @FXML public void onLogout(ActionEvent event) { Navigator.getInstance().logout(event); }
    @FXML public void onProfile(ActionEvent event) { Navigator.getInstance().goTo(event, PROFILEFXML); }
    @FXML public void onHome(ActionEvent event) { Navigator.getInstance().goToHome(event); }

    private void showError(String message) {
        if (lblError != null) { lblError.setText(message); lblError.setVisible(true); }
    }
    private void hideError() {
        if (lblError != null) { lblError.setVisible(false); }
    }
}