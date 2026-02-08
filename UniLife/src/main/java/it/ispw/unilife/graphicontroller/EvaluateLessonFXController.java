package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.LessonBean;
import it.ispw.unilife.bean.NotificationBean;
import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.controller.AddTutor;
import it.ispw.unilife.controller.NotificationSystem;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvaluateLessonFXController {

    private static final Logger logger = Logger.getLogger(EvaluateLessonFXController.class.getName());

    @FXML private Label lblTutorName;
    @FXML private Label lblSubject;
    @FXML private Label lblStartTime;
    @FXML private Label lblEndTime;
    @FXML private Label lblDuration;

    private TokenBean tokenBean;
    private LessonBean currentLesson;
    private final AddTutor addTutorController = new AddTutor();
    private final NotificationSystem notificationSystem = NotificationSystem.getInstance();
    private static final String ERRORE = "ERRORE";

    @FXML
    public void initialize() {
        Object data = Navigator.getInstance().getCurrentData();
        tokenBean = Navigator.getInstance().getCurrentToken();

        // Ci aspettiamo che il ProfileController ci passi direttamente la Notifica
        if (data instanceof NotificationBean notificationBean) {
            NotificationBean currentNotification = notificationBean;

            try {
                // Chiamiamo il controller applicativo per recuperare i dettagli della lezione
                // partendo dalla notifica ricevuta
                this.currentLesson = notificationSystem.getLessonFromNotification(tokenBean, currentNotification);

                if (this.currentLesson != null) {
                    populateView();
                } else {
                    logger.warning("Nessuna lezione trovata associata a questa notifica.");
                    showAlert("Attenzione", "Impossibile recuperare i dettagli della lezione.");
                }

            } catch (DAOException e) {
                logger.log(Level.SEVERE, "Errore nel recupero della lezione", e);
                showAlert(ERRORE, "Errore di comunicazione col database.");
            }

        } else {
            logger.severe("Dato passato al Navigator non valido. Atteso NotificationBean.");
            showAlert(ERRORE, "Dati di navigazione errati.");
        }
    }

    private void populateView() {
        lblTutorName.setText(currentLesson.getTutor().getName());
        lblSubject.setText(currentLesson.getSubject());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        if (currentLesson.getStartTime() != null) {
            lblStartTime.setText(currentLesson.getStartTime().format(formatter));
        }

        if (currentLesson.getEndTime() != null) {
            lblEndTime.setText(currentLesson.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        lblDuration.setText(currentLesson.getDurationInHours() + " Hours");
    }

    @FXML
    public void onAccept(ActionEvent event) {
        if (currentLesson == null) return;

        try {
            addTutorController.acceptLesson(tokenBean, currentLesson);
            showAlert("Successo", "Lezione accettata correttamente.");
            Navigator.getInstance().goToHome(event);

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Errore nella valutazione lezione", e);
            showAlert(ERRORE, "Impossibile completare l'operazione.");
        }
    }

    @FXML
    public void onReject(ActionEvent event) {
        if (currentLesson == null) return;

        try {
            addTutorController.rejectLesson(tokenBean, currentLesson);

            showAlert("Successo", "Lezione rifiutata correttamente.");
            Navigator.getInstance().goToHome(event);

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Errore nella valutazione lezione", e);
            showAlert(ERRORE, "Impossibile completare l'operazione.");
        }
    }

    @FXML
    public void onBack(ActionEvent event) {
        Navigator.getInstance().goTo(event, "Profile.fxml");
    }

    @FXML
    public void onLogout(ActionEvent event) {
        Navigator.getInstance().logout(event);
    }

    @FXML
    public void onHome(ActionEvent event) {
        Navigator.getInstance().goToHome(event);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}