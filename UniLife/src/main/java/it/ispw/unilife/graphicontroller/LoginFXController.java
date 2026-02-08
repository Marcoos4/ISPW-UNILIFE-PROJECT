package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.bean.UserBean;
import it.ispw.unilife.controller.LoginController;
import it.ispw.unilife.exception.ExternalAuthenticationException;
import it.ispw.unilife.exception.ExternalUserNotFoundException;
import it.ispw.unilife.exception.LoginException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginFXController {

    private static final Logger LOGGER = Logger.getLogger(LoginFXController.class.getName());

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private final LoginController loginController;

    public LoginFXController() {
        this.loginController = new LoginController();
    }

    @FXML
    public void initialize() {
        if (lblError != null) {
            lblError.setVisible(false);
        }
    }

    @FXML
    public void onLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username == null || username.trim().isEmpty()) {
            showError("Username is required");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            showError("Password is required");
            return;
        }

        try {
            UserBean userBean = new UserBean();
            userBean.setUserName(username.trim());
            userBean.setPassword(password);

            TokenBean tokenBean = loginController.login(userBean);

            Navigator.getInstance().setCurrentToken(tokenBean);
            Navigator.getInstance().goToHome(event);

        } catch (LoginException e) {
            LOGGER.log(Level.WARNING, "Login failed", e);
            showError("Invalid username or password");
        }
    }

    @FXML
    public void onRegister(ActionEvent event) {
        Navigator.getInstance().goTo(event, "Register.fxml");
    }

    @FXML
    public void onGithubLogin(ActionEvent event) {
        handleExternalLogin(event, "GitHub");
    }

    @FXML
    public void onGoogleLogin(ActionEvent event) {
        handleExternalLogin(event, "Google");
    }

    private void handleExternalLogin(ActionEvent event, String serviceName) {
        try {
            // Tenta il login. Se l'utente non esiste, il controller lancia ExternalUserNotFoundException
            TokenBean tokenBean = loginController.externalLogin(serviceName);

            // Se arriviamo qui, il login ha avuto successo
            Navigator.getInstance().setCurrentToken(tokenBean);
            Navigator.getInstance().goToHome(event);

        } catch (ExternalUserNotFoundException e) {
            // CASO UTENTE NON TROVATO: Recuperiamo i dati parziali (Nome, Cognome, Email)
            UserBean externalData = e.getUserBean();

            // Mostriamo l'Alert all'utente
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Utente non registrato");
            alert.setHeaderText("Account " + serviceName + " non trovato");
            alert.setContentText("Nessun account associato a questo utente " + serviceName + ".\nVuoi procedere alla registrazione con questi dati?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Passiamo i dati alla vista di registrazione tramite il Navigator
                Navigator.getInstance().setCurrentData(externalData);
                Navigator.getInstance().goTo(event, "Register.fxml");
            }

        } catch (ExternalAuthenticationException e) {
            LOGGER.log(Level.WARNING, "{0} login failed", serviceName);
            showError(serviceName + " authentication failed. " + e.getMessage());
        } catch (UserNotFoundException e) {
            // Catch generico di sicurezza
            showError("Utente non trovato.");
        }
    }

    @FXML
    public void onHome(ActionEvent event) {
        Navigator.getInstance().goToLandingPage(event);
    }

    private void showError(String message) {
        if (lblError != null) {
            lblError.setText(message);
            lblError.setVisible(true);
        }
    }
}