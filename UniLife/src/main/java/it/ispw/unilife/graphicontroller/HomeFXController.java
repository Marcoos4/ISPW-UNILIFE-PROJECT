package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.view.Navigator;
import it.ispw.unilife.view.homepage.HomePageComponent;
import it.ispw.unilife.view.homepage.HomePageFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

public class HomeFXController {

    @FXML private BorderPane rootPane;
    @FXML private HBox headerBar;
    @FXML private HBox headerButtons;
    @FXML private VBox contentArea;

    @FXML private Button btnHome;
    @FXML private Button btnLogout;
    @FXML private Button btnProfile;

    @FXML private Button btnBookTutor;
    @FXML private Button btnCourseDiscovery;

    @FXML
    public void initialize() {
        HomePageComponent homeComponent = HomePageFactory.createHomePage();

        homeComponent.decorateContent(contentArea);

        if (btnLogout != null) {
            btnLogout.setVisible(homeComponent.showLogout());
            btnLogout.setManaged(homeComponent.showLogout());
        }
        if (btnProfile != null) {
            btnProfile.setVisible(homeComponent.showProfile());
            btnProfile.setManaged(homeComponent.showProfile());
        }
    }

    @FXML
    public void onHome(ActionEvent event) {
        Navigator.getInstance().goToHome(event);
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
    public void onBookTutor(ActionEvent event) {
        Navigator.getInstance().goTo(event, "SearchTutor.fxml");
    }

    @FXML
    public void onCourseDiscovery(ActionEvent event) {
        Navigator.getInstance().goTo(event, "SearchCourse.fxml");
    }
}
