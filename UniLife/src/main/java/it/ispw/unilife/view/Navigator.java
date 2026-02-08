package it.ispw.unilife.view;

import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.bean.UserBean;
import it.ispw.unilife.controller.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Navigator class for handling page navigation in the JavaFX application.
 * Implements the Singleton pattern.
 */
public class Navigator {

    private static final Logger LOGGER = Logger.getLogger(Navigator.class.getName());
    private static Navigator instance = null;
    private static final String FXML_PATH = System.getProperty("fxml.path", "/viewFXML/");

    private TokenBean currentToken;
    private Object currentData;

    LoginController loginController = new LoginController();

    private Navigator() {
    }

    public static synchronized Navigator getInstance() {
        if (instance == null) {
            instance = new Navigator();
        }
        return instance;
    }

    /**
     * Navigate to a new page.
     * @param event The ActionEvent that triggered the navigation
     * @param fxmlFile The FXML file to navigate to
     */
    public void goTo(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + fxmlFile));
            Parent root = loader.load();
            Stage stage = getStageFromEvent(event);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating to {0}", fxmlFile);
        }
    }

    /**
     * Navigate to a new page and pass data to the controller.
     * @param event The ActionEvent that triggered the navigation
     * @param fxmlFile The FXML file to navigate to
     * @param data The data to pass to the controller
     */
    public void goToWithData(ActionEvent event, String fxmlFile, Object data) {
        this.currentData = data;
        goTo(event, fxmlFile);
    }

    /**
     * Navigate to the appropriate home page based on the user's role.
     * @param event The ActionEvent that triggered the navigation
     */
    public void goToHome(ActionEvent event) {
        goTo(event, "Home.fxml");
    }

    /**
     * Navigate to the landing page (Guest home).
     * @param event The ActionEvent that triggered the navigation
     */
    public void goToLandingPage(ActionEvent event) {
        goTo(event, "LandingPage.fxml");
    }

    /**
     * Perform logout and navigate to landing page.
     * @param event The ActionEvent that triggered the navigation
     */
    public void logout(ActionEvent event) {
        if (currentToken != null && currentToken.getToken() != null) {
            loginController.invalidateToken(currentToken);
        }
        currentToken = null;
        currentData = null;
        goToLandingPage(event);
    }

    /**
     * Get the current user's role.
     * @return The user's role or null if not logged in
     */
    public String getCurrentUserRole() {

        if (currentToken == null || currentToken.getToken() == null) {
            return null;
        }

        UserBean bean = loginController.findUserRole(currentToken);
        return bean != null ? bean.getRole() : null;
    }

    /**
     * Check if a user is currently logged in.
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentToken != null &&
               currentToken.getToken() != null &&
               loginController.checkTokenValidity(currentToken);
    }

    public TokenBean getCurrentToken() {
        return currentToken;
    }

    public void setCurrentToken(TokenBean token) {
        this.currentToken = token;
    }

    public Object getCurrentData() {
        return currentData;
    }

    public void setCurrentData(Object data) {
        this.currentData = data;
    }

    public void clearCurrentData() {
        this.currentData = null;
    }

    private Stage getStageFromEvent(ActionEvent event) {
        Node source = (Node) event.getSource();
        return (Stage) source.getScene().getWindow();
    }

    /**
     * Navigate using a stage directly (useful for initialization).
     * @param stage The stage to use
     * @param fxmlFile The FXML file to navigate to
     */
    public void goTo(Stage stage, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating to {0}", fxmlFile);
        }
    }

}
