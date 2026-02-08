package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.NotificationBean;
import it.ispw.unilife.bean.ReservationBean;
import it.ispw.unilife.controller.BookTutor;
import it.ispw.unilife.controller.NotificationSystem;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import java.time.format.DateTimeFormatter;

import java.util.Optional;

public class LessonRequestFXController {

    @FXML private Button btnLogout;
    @FXML private Button btnProfile;
    @FXML private Button btnHome;

    @FXML private Button btnReject;
    @FXML private Button btnAccept;

    @FXML private Label lblLessonTime;
    @FXML private Label lblLessonSubject;
    @FXML private Label lblStudentName;
    @FXML private Label lblLessonInfo;

    private final BookTutor bookTutorController;
    private ReservationBean currentReservation;
    private NotificationSystem notificationSystem = NotificationSystem.getInstance();
    private static final String PROFILEFXML = "Profile.fxml";

    public LessonRequestFXController() {
        this.bookTutorController = new BookTutor();
    }

    @FXML
    public void initialize() throws DAOException {
        Object data = Navigator.getInstance().getCurrentData();
        if (data instanceof NotificationBean notificationBean) {
            NotificationBean currentNotification = notificationBean;
            currentReservation = notificationSystem.resolveReservationNotification(currentNotification, Navigator.getInstance().getCurrentToken());
            populateRequestDetails(currentReservation);
        }
    }

    private void populateRequestDetails(ReservationBean reservation) {
        if (lblStudentName != null && reservation.getStudent() != null) {
            lblStudentName.setText(reservation.getStudent().getName() + " " + reservation.getStudent().getSurname());
        }

        if (lblLessonTime != null && reservation.getLesson() != null && reservation.getLesson().getStartTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedTime = reservation.getLesson().getStartTime().format(formatter);
            lblLessonTime.setText(formattedTime);
        }

        if (lblLessonInfo != null && reservation.getLesson() != null) {
            StringBuilder info = new StringBuilder();
            info.append("Subject: ").append(reservation.getLesson().getSubject()).append("\n");
            info.append("Duration: ").append(reservation.getLesson().getDurationInHours()).append(" hours\n");
            info.append("Price: ").append(reservation.getLesson().getPrice()).append(" EUR");
            lblLessonInfo.setText(info.toString());
        }

        if (lblLessonSubject != null && reservation.getLesson() != null) {
            lblLessonSubject.setText(reservation.getLesson().getSubject());
        }
    }

    @FXML
    public void onReject(ActionEvent event) {
        try {
            if (currentReservation != null && Navigator.getInstance().getCurrentToken() != null) {
                bookTutorController.abortReservationProcedure(Navigator.getInstance().getCurrentToken(), currentReservation);
            }
            Navigator.getInstance().clearCurrentData();
            Navigator.getInstance().goTo(event, PROFILEFXML);
        } catch (DAOException | UserNotFoundException e) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Reject Rifiutato");
            alert.setHeaderText("Lesson non trovata ");
            alert.setContentText("Nessuna lezione trovata.\nVuoi tornare alla home o andare nel profilo?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Navigator.getInstance().goTo(event, "Home.fxml");
            }else{
                Navigator.getInstance().goTo(event, PROFILEFXML);
            }
        }
    }

    @FXML
    public void onAccept(ActionEvent event) {
        try {
            if (currentReservation != null && Navigator.getInstance().getCurrentToken() != null) {
                bookTutorController.acceptReservationProcedure(Navigator.getInstance().getCurrentToken(), currentReservation);
            }
            Navigator.getInstance().clearCurrentData();
            Navigator.getInstance().goTo(event, PROFILEFXML);
        } catch (DAOException | UserNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Accept Rifiutato");
            alert.setHeaderText("Lesson non trovata ");
            alert.setContentText("Nessuna lezione trovata.\nVuoi tornare alla home o andare nel profilo?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Navigator.getInstance().goTo(event, "Home.fxml");
            }else{
                Navigator.getInstance().goTo(event, PROFILEFXML);
            }
        }
    }

    @FXML
    public void onLogout(ActionEvent event) {
        Navigator.getInstance().logout(event);
    }

    @FXML
    public void onProfile(ActionEvent event) {
        Navigator.getInstance().goTo(event, PROFILEFXML);
    }

    @FXML
    public void onHome(ActionEvent event) {
        Navigator.getInstance().goToHome(event);
    }
}
