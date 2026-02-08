package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
public class CourseReviewsFXController {

    @FXML private Button btnLogout;
    @FXML private Button btnProfile;
    @FXML private Button btnHome;

    @FXML
    public void onLogout(ActionEvent event) {
        Navigator.getInstance().goTo(event, "Login.fxml");
    }

    @FXML
    public void onProfile(ActionEvent event) {
        Navigator.getInstance().goTo(event, "Profile.fxml");
    }

}