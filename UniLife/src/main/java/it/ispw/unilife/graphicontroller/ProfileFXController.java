package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.controller.LoginController;
import it.ispw.unilife.controller.NotificationSystem;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.view.Navigator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileFXController {

    private static final Logger logger = Logger.getLogger(ProfileFXController.class.getName());

    @FXML private Button btnLogout;
    @FXML private Button btnHome;
    @FXML private VBox notificationContainer;
    @FXML private ImageView imgProfile;
    @FXML private Label lblUsername;
    @FXML private Label lblName;
    @FXML private Label lblSurname;
    @FXML private Label lblRole;

    private final LoginController loginController = new LoginController();
    private final NotificationSystem notificationSystem = NotificationSystem.getInstance();

    @FXML
    public void initialize() {
        loadUserProfile();
        loadNotifications();
    }

    private void loadUserProfile() {
        UserBean user = loginController.getProfile(Navigator.getInstance().getCurrentToken());
        if (user != null) {
            lblUsername.setText("Username: " + user.getUserName());
            lblName.setText("Name: " + user.getName());
            lblSurname.setText("Surname: " + user.getSurname());
            lblRole.setText("Role: " + Navigator.getInstance().getCurrentUserRole());
        }
    }

    private void loadNotifications() {
        notificationContainer.getChildren().clear();

        try {
            TokenBean tokenBean = Navigator.getInstance().getCurrentToken();
            List<NotificationBean> notifications = notificationSystem.getNotifications(tokenBean);

            if (notifications.isEmpty()) {
                Label noNotif = new Label("Nessuna notifica presente.");
                noNotif.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
                notificationContainer.getChildren().add(noNotif);
                return;
            }

            // --- MODIFICA: Recupero il profilo UNA SOLA VOLTA qui fuori ---
            // Uso l'istanza di classe 'loginController' definita in alto
            UserBean currentUser = loginController.getProfile(tokenBean);
            String currentRole = (currentUser != null) ? currentUser.getRole() : "";
            // -------------------------------------------------------------

            for (NotificationBean notif : notifications) {
                // Passo il ruolo al metodo
                HBox card = createNotificationCard(notif, currentRole);
                notificationContainer.getChildren().add(card);
            }

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Errore nel caricamento delle notifiche", e);
            Label errorLabel = new Label("Errore nel caricamento delle notifiche.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-style: italic;");
            notificationContainer.getChildren().add(errorLabel);
        }
    }

    // Aggiunto parametro String userRole
    private HBox createNotificationCard(NotificationBean notif, String userRole) {
        HBox card = new HBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0); -fx-padding: 15; -fx-background-radius: 5;");

        VBox textBox = new VBox(5.0);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String timeStr = notif.getTimestamp() != null ? notif.getTimestamp().format(formatter) : "--";
        Label lblTime = new Label(timeStr);
        lblTime.setTextFill(Color.web("#ff9933"));
        lblTime.setFont(new Font(12.0));

        Label lblFrom = new Label("From: " + notif.getSenderUsername());
        lblFrom.setFont(Font.font("System Bold", 14.0));

        Label lblMsg = new Label(notif.getMessage());
        lblMsg.setWrapText(true);

        Label lblType = new Label("[" + notif.getNotificationType() + "]");
        lblType.setTextFill(Color.web("#999"));
        lblType.setFont(new Font(11.0));

        Label lblStatus = new Label("Status: " + notif.getStatus());
        lblStatus.setFont(new Font(11.0));
        lblStatus.setTextFill("Pending".equals(notif.getStatus()) ? Color.web("#cc6600") : Color.web("#339933"));

        textBox.getChildren().addAll(lblTime, lblFrom, lblMsg, lblType, lblStatus);
        card.getChildren().add(textBox);

        // --- MODIFICA: Uso il parametro userRole passato, niente chiamate al DB qui ---
        String type = notif.getNotificationType();

        // Logica corretta con equalsIgnoreCase
        boolean isStudentApp = "APPLICATION".equals(type) && "Student".equalsIgnoreCase(userRole);
        boolean isTutorLesson = "LESSON".equals(type) && "Tutor".equalsIgnoreCase(userRole);

        // Mostra il bottone solo se NON è uno dei casi da bloccare
        if (!isStudentApp && !isTutorLesson) {
            Button btnOpen = new Button("Open");
            btnOpen.setStyle("-fx-background-color: #ff9933; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
            btnOpen.setOnAction(e -> {
                try {
                    onOpenNotification(notif, e);
                } catch (DAOException ex) {
                    logger.log(Level.SEVERE, "Errore nel caricamento delle notifiche", ex);
                }
            });
            card.getChildren().add(btnOpen);
        }
        // -----------------------------------------------------------------------------

        return card;
    }
    private void onOpenNotification(NotificationBean notif, ActionEvent event) throws DAOException {
        TokenBean tokenBean = Navigator.getInstance().getCurrentToken();

        switch (notif.getNotificationType()) {
            case "RESERVATION":
                ReservationBean reservationBean = notificationSystem.resolveReservationNotification(notif, tokenBean);
                String status = reservationBean.getStatus();
                if ("Pending".equals(status)) {
                    Navigator.getInstance().goToWithData(event, "LessonRequest.fxml", notif);
                } else if ("Confirmed".equals(status)) {
                    Navigator.getInstance().goToWithData(event, "Payment.fxml", notif);
                }
                break;
            case "APPLICATION":
                // 1. Risolvi la notifica per ottenere i dati dell'Application
                ApplicationBean appBean = notificationSystem.resolveApplicationNotification(notif, tokenBean);

                if (appBean != null) {
                    String currentUserRole = Navigator.getInstance().getCurrentUserRole(); // Assumendo tu abbia questo metodo o simile

                    if ("UNIVERSITY_EMPLOYEE".equalsIgnoreCase(currentUserRole)) {
                        EvaluationContextBean context = new EvaluationContextBean(appBean, notif);

                        Navigator.getInstance().goToWithData(event, "EvaluateApplication.fxml", context);
                    } else {
                        // Se sono uno studente, vado alla pagina di dettaglio/stato
                        Navigator.getInstance().goToWithData(event, "ApplicationStatus.fxml", appBean);
                    }

                } else {
                    logger.warning("Impossibile recuperare i dettagli dell'Application.");
                }
                break;
            case "LESSON":
                Navigator.getInstance().goToWithData(event, "EvaluateLesson.fxml", notif);
                break;
            default:
                break;
        }

        logger.info("Notifica aperta: " + notif.getMessage());
    }

    @FXML
    public void onLogout(ActionEvent event) {
        Navigator.getInstance().logout(event);
    }

    @FXML
    public void onHome(ActionEvent event) {
        Navigator.getInstance().goToHome(event);
    }
}

