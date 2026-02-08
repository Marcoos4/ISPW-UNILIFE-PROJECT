package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.bean.UniversityBean;
import it.ispw.unilife.bean.UserBean;
import it.ispw.unilife.controller.LoginController;
import it.ispw.unilife.enums.Role;
import it.ispw.unilife.exception.RegistrationException;
import it.ispw.unilife.view.Navigator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterFXController {

    private static final Logger LOGGER = Logger.getLogger(RegisterFXController.class.getName());

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtName;
    @FXML private TextField txtSurname;

    @FXML private ComboBox<String> cmbRole;
    @FXML private ComboBox<String> cmbUniversity;
    @FXML private Label lblUniversity;
    @FXML private Label lblError;

    @FXML private Button btnRegister;
    @FXML private Button btnBackToLogin;

    private final LoginController loginController;

    private static final String UNIVERSITYEMPLOYEE = "University Employee";

    public RegisterFXController() {
        this.loginController = new LoginController();
    }

    @FXML
    public void initialize() {
        if (cmbRole != null) {
            cmbRole.setItems(FXCollections.observableArrayList(
                    "Student", "Tutor", UNIVERSITYEMPLOYEE
            ));

            cmbRole.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                boolean isEmployee = UNIVERSITYEMPLOYEE.equals(newVal);
                showUniversityField(isEmployee);
            });
        }

        // 2. Configurazione ComboBox Università (Dinamica)
        if (cmbUniversity != null) {
            try {
                List<UniversityBean> uniBeans = loginController.findAvailableUniversities();
                List<String> uniNames = new ArrayList<>();
                for (UniversityBean bean : uniBeans) {
                    uniNames.add(bean.getName());
                }
                cmbUniversity.setItems(FXCollections.observableArrayList(uniNames));
            } catch (Exception e) {
                LOGGER.severe("Errore caricamento università: " + e.getMessage());
            }
        }

        if (lblError != null) lblError.setVisible(false);

        // 3. RECUPERO DATI ESTERNI (Pre-fill)
        Object data = Navigator.getInstance().getCurrentData();
        if (data instanceof UserBean) {
            UserBean externalUserData = (UserBean) data;
            prefillForm(externalUserData);
        }
    }

    private void prefillForm(UserBean user) {
        if (user != null) {
            if (txtUsername != null) {
                txtUsername.setText(user.getUserName());
                txtUsername.setDisable(true);
            }
            if (txtName != null) txtName.setText(user.getName());
            if (txtSurname != null) txtSurname.setText(user.getSurname());

            // Impostiamo un ruolo di default se vogliamo, es. Student
            if (cmbRole != null) cmbRole.setValue("Student");
        }
    }

    private void showUniversityField(boolean show) {
        if (cmbUniversity != null) {
            cmbUniversity.setVisible(show);
            cmbUniversity.setManaged(show);
            if (!show) cmbUniversity.setValue(null);
        }
        if (lblUniversity != null) {
            lblUniversity.setVisible(show);
            lblUniversity.setManaged(show);
        }
    }

    @FXML
    public void onRegister(ActionEvent event) {
        hideError();

        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String name = txtName.getText();
        String surname = txtSurname.getText();
        String roleStr = cmbRole.getValue();
        String university = cmbUniversity.getValue();

        // Validazione
        if (username == null || username.trim().isEmpty()) {
            showError("Username is required");
            return;
        }

        // La password è obbligatoria anche per utenti esterni per creare il record locale
        // (A meno che tu non voglia generarla automaticamente)
        if (password == null || password.trim().isEmpty()) {
            showError("Password is required (create a password for this app)");
            return;
        }

        if (name == null || name.trim().isEmpty()) {
            showError("Name is required");
            return;
        }
        if (surname == null || surname.trim().isEmpty()) {
            showError("Surname is required");
            return;
        }
        if (roleStr == null) {
            showError("Role is required");
            return;
        }
        if (UNIVERSITYEMPLOYEE.equals(roleStr) && (university == null || university.isEmpty())) {
            showError("Select a University");
            return;
        }

        try {
            UserBean userBean = new UserBean();
            userBean.setUserName(username.trim());
            userBean.setPassword(password);
            userBean.setName(name.trim());
            userBean.setSurname(surname.trim());
            userBean.setRole(convertStringToRole(roleStr).name()); // Usiamo name() dell'enum
            userBean.setUniversity(university);

            // Chiamata al controller per registrare
            TokenBean tokenBean = loginController.register(userBean);

            // Successo -> Vai alla Home
            Navigator.getInstance().setCurrentToken(tokenBean);
            Navigator.getInstance().clearCurrentData(); // Puliamo i dati temporanei
            Navigator.getInstance().goToHome(event);

        } catch (RegistrationException e) {
            LOGGER.log(Level.WARNING, "Registration failed", e);
            showError(e.getMessage());
        }
    }

    private Role convertStringToRole(String roleStr) {
        switch (roleStr) {
            case "Tutor": return Role.TUTOR;
            case UNIVERSITYEMPLOYEE: return Role.UNIVERSITY_EMPLOYEE;
            default: return Role.STUDENT;
        }
    }

    @FXML
    public void onBackToLogin(ActionEvent event) {
        Navigator.getInstance().clearCurrentData(); // Importante pulire se si torna indietro
        Navigator.getInstance().goTo(event, "Login.fxml");
    }

    @FXML
    public void onHome(ActionEvent event) {
        Navigator.getInstance().clearCurrentData();
        Navigator.getInstance().goToLandingPage(event);
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