package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class LandingPageFXController {

    @FXML private Button btnLoginRegister;
    @FXML private Button btnHome;

    @FXML
    public void initialize() {
        if (btnLoginRegister != null) {
            btnLoginRegister.setOnAction(this::onLoginRegister);
        }
    }

    @FXML
    public void onLoginRegister(ActionEvent event) {
        Navigator.getInstance().goTo(event, "Login.fxml");
    }

    @FXML
    public void onHome(ActionEvent event) {
        Navigator.getInstance().goToLandingPage(event);
    }
}
