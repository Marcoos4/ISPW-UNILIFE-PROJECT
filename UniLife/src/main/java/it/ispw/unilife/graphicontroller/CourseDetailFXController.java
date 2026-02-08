package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.CourseBean;
import it.ispw.unilife.controller.CourseDiscoveryAndApplication;
import it.ispw.unilife.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CourseDetailFXController {

    @FXML private Button btnLogout;
    @FXML private Button btnProfile;
    @FXML private Button btnHome;

    @FXML private Label lblCourseName;
    @FXML private Label lblDescription;

    // Course Info Labels
    @FXML private Label lblDuration;
    @FXML private Label lblFees;
    @FXML private Label lblLanguage;
    @FXML private Label lblCourseType;

    // University Info Labels
    @FXML private Label lblUniName;
    @FXML private Label lblUniLocation;
    @FXML private Label lblUniLivingCost;
    @FXML private Label lblUniRanking;

    @FXML private Button btnBack;
    @FXML private Button btnFav;
    @FXML private Button btnReviews;
    @FXML private Button btnApply;

    private CourseBean selectedCourse;

    private static final Logger LOGGER = Logger.getLogger(CourseDetailFXController.class.getName());

    private CourseDiscoveryAndApplication courseController = new CourseDiscoveryAndApplication();

    @FXML
    public void initialize() {
        Object data = Navigator.getInstance().getCurrentData();

        if (data instanceof CourseBean) {
             selectedCourse = courseController.findCourseInformation((CourseBean) data);
            populateDetails();
        } else {
            lblCourseName.setText("Nessun corso selezionato");
            clearLabels();
        }
    }

    private void populateDetails() {
        if (selectedCourse != null) {
            // Header e Descrizione
            lblCourseName.setText(selectedCourse.getTitle());
            lblDescription.setText(selectedCourse.getDescription());

            // Course Info
            lblDuration.setText(String.valueOf(selectedCourse.getDuration()) + " months");
            lblFees.setText(String.format("€ %.2f", selectedCourse.getFees()));
            lblLanguage.setText(selectedCourse.getLanguageOfInstruction());
            lblCourseType.setText(selectedCourse.getCourseType());

            // University Info
            if (selectedCourse.getUniversity() != null) {
                lblUniName.setText(selectedCourse.getUniversity().getName());
                lblUniLocation.setText(selectedCourse.getUniversity().getLocation());
                lblUniLivingCost.setText(String.format("€ %.2f", selectedCourse.getUniversity().getLivingCosts()));
                lblUniRanking.setText("#" + selectedCourse.getUniversity().getRanking());
            } else {
                lblUniName.setText("N/A");
                lblUniLocation.setText("N/A");
            }

            LOGGER.log(Level.INFO,"Dettagli caricati per: {0}", selectedCourse.getTitle());
        }
    }

    private void clearLabels() {
        lblDescription.setText("");
        lblDuration.setText("");
        lblFees.setText("");
        lblLanguage.setText("");
        lblCourseType.setText("");
        lblUniName.setText("");
        lblUniLocation.setText("");
        lblUniLivingCost.setText("");
        lblUniRanking.setText("");
    }

    @FXML
    public void onBack(ActionEvent event) {
        // Torna alla pagina di ricerca (SearchCourse)
        Navigator.getInstance().goTo(event, "SearchCourse.fxml");
    }

    @FXML
    public void onAddToFav(ActionEvent event) {
        LOGGER.info("Aggiunto ai preferiti: {(selectedCourse != null ? selectedCourse.getTitle() : \"null\")}"  );
    }

    @FXML
    public void onGoToReviews(ActionEvent event) {
        // Passa il corso corrente alla pagina delle recensioni
        Navigator.getInstance().setCurrentData(selectedCourse);
        Navigator.getInstance().goTo(event, "CourseReviews.fxml");
    }

    @FXML
    public void onApply(ActionEvent event) {
        // Passa il corso corrente al form di applicazione
        Navigator.getInstance().setCurrentData(selectedCourse);
        Navigator.getInstance().goTo(event, "ApplicationForm.fxml");
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
}