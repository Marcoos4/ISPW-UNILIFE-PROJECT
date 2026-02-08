package it.ispw.unilife.config;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UniLIFEApp extends Application {

    private static final Logger LOGGER = Logger.getLogger(UniLIFEApp.class.getName());

    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewFXML/LandingPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("UniLife");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading application", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
