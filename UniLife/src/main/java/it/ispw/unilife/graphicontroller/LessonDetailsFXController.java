package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.LessonBean;
import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.controller.AddTutor;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.EmployeeNotFoundException;
import it.ispw.unilife.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LessonDetailsFXController {

    private static final Logger LOGGER = Logger.getLogger(LessonDetailsFXController.class.getName());

    @FXML private Button btnLogout;
    @FXML private Button btnProfile;
    @FXML private Button btnHome;

    @FXML private Button btnBack;
    @FXML private Button btnConfirm;

    @FXML private TextField txtSubject;
    @FXML private ComboBox<String> cmbDuration;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> cmbStartTime;
    @FXML private TextField txtPrice;
    @FXML private Label lblError;

    private final AddTutor addTutorController;

    public LessonDetailsFXController() {
        this.addTutorController = new AddTutor();
    }

    @FXML
    public void initialize() {
        if (cmbDuration != null) {
            cmbDuration.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9");
        }

        if (cmbStartTime != null) {
            for (int i = 8; i <= 20; i++) {
                cmbStartTime.getItems().add(String.format("%02d:00", i));
                cmbStartTime.getItems().add(String.format("%02d:30", i));
            }
        }

        if (lblError != null) {
            lblError.setVisible(false);
        }
    }

    @FXML
    public void onLogout(ActionEvent event) {
        Navigator.getInstance().logout(event);
    }

    @FXML
    public void onProfile(ActionEvent event) {
        Navigator.getInstance().goTo(event, "Profile.fxml");
    }

    @FXML
    public void onHome(ActionEvent event) {
        Navigator.getInstance().goToHome(event);
    }

    @FXML
    public void onBack(ActionEvent event) {
        Navigator.getInstance().goToHome(event);
    }

    @FXML
    public void onConfirmLesson(ActionEvent event) {
        hideError();

        try {
            // 1. Validazione Campi Obbligatori (Lancia IllegalArgumentException se fallisce)
            validateMandatoryFields();

            // 2. Parsing e Costruzione del Bean (Incapsula logica Date/Time e Numeri)
            LessonBean lessonBean = buildLessonBeanFromUI();

            // 3. Invio al Controller Applicativo
            submitLesson(lessonBean);

            // 4. Navigazione
            Navigator.getInstance().goToHome(event);

        } catch (IllegalArgumentException e) {
            // Gestisce errori di validazione (es. campi vuoti, formato numeri errato)
            showError(e.getMessage());
        } catch (Exception e) {
            // Gestisce errori di sistema (DAO, EmployeeNotFound, ecc.)
            LOGGER.log(Level.WARNING, "Error adding lesson", e);
            showError("Error adding lesson: " + e.getMessage());
        }
    }

    // --- METODI HELPER (LOGICA ESTRATTA) ---

    /**
     * Controlla che tutti i campi UI siano popolati.
     */
    private void validateMandatoryFields() {
        if (txtSubject.getText() == null || txtSubject.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject is required");
        }
        if (cmbDuration.getValue() == null) {
            throw new IllegalArgumentException("Duration is required");
        }
        if (datePicker.getValue() == null) {
            throw new IllegalArgumentException("Date is required");
        }
        if (cmbStartTime.getValue() == null) {
            throw new IllegalArgumentException("Start time is required");
        }
        if (txtPrice.getText() == null || txtPrice.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Price is required");
        }
    }

    /**
     * Costruisce il LessonBean parsando i valori dalla UI.
     * Gestisce internamente la conversione delle Date e dei Numeri.
     */
    private LessonBean buildLessonBeanFromUI() {
        try {
            String subject = txtSubject.getText().trim();
            int duration = Integer.parseInt(cmbDuration.getValue());
            float price = Float.parseFloat(txtPrice.getText().trim());

            // Calcolo Data e Ora
            LocalDateTime startTime = calculateStartDateTime();
            LocalDateTime endTime = startTime.plusHours(duration);

            LessonBean bean = new LessonBean();
            bean.setSubject(subject);
            bean.setDurationInHours(duration);
            bean.setPrice(price);
            bean.setStartTime(startTime);
            bean.setEndTime(endTime);

            return bean;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format for Duration or Price.");
        }
    }

    /**
     * Helper specifico per la logica di combinazione Data + Ora (Stringa HH:mm).
     */
    private LocalDateTime calculateStartDateTime() {
        LocalDate date = datePicker.getValue();
        String timeStr = cmbStartTime.getValue(); // Formato atteso "HH:mm"

        String[] timeParts = timeStr.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        return LocalDateTime.of(date, LocalTime.of(hour, minute));
    }

    /**
     * Gestisce l'interazione con il Controller e il Token.
     */
    private void submitLesson(LessonBean lessonBean) throws DAOException, EmployeeNotFoundException {
        TokenBean token = Navigator.getInstance().getCurrentToken();
        if (token != null) {
            addTutorController.startTutorLessonProcedure(token, lessonBean);
        } else {
            throw new IllegalStateException("User session not found (Token is null)");
        }
    }

    private void showError(String message) {
        if (lblError != null) {
            lblError.setText(message);
            lblError.setVisible(true);
        }
    }

    private void hideError() {
        if (lblError != null) {
            lblError.setVisible(false);
        }
    }
}
