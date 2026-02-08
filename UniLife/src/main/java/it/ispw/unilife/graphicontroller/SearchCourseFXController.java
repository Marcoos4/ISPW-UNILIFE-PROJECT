package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.CourseBean;
import it.ispw.unilife.bean.FilterCourseBean;
import it.ispw.unilife.controller.CourseDiscoveryAndApplication;
import it.ispw.unilife.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchCourseFXController {


    @FXML private Button btnLogout;
    @FXML private Button btnProfile;
    @FXML private Button btnHome;

    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;
    @FXML private Button btnApplyFilters;

    @FXML private ComboBox<String> cmbUniversityName;
    @FXML private ComboBox<String> cmbLocation;
    @FXML private ComboBox<String> cmbCourseType;
    @FXML private ComboBox<String> cmbDuration;
    @FXML private ComboBox<String> cmbRanking;
    @FXML private ComboBox<String> cmbLanguage;

    @FXML private VBox courseListContainer;

    private final CourseDiscoveryAndApplication courseController;

    public SearchCourseFXController() {
        this.courseController = new CourseDiscoveryAndApplication();
    }

    @FXML
    public void initialize() {
        populateFilters();
        loadCourses(null);
    }

    // 1. IL COORDINATORE (Orchestrator)
    // Complessità: 1 (È solo una lista sequenziale di chiamate)
    private void populateFilters() {
        List<FilterCourseBean> filters = courseController.listSearchFilter();

        // 1. Inizializzazione dei Set
        Set<String> universityNames = new HashSet<>();
        Set<String> locations = new HashSet<>();
        Set<String> courseTypes = new HashSet<>();
        Set<String> durations = new HashSet<>();
        Set<String> rankings = new HashSet<>();
        Set<String> languages = new HashSet<>();

        // 2. Iterazione unica (più efficiente che fare 6 stream separati)
        for (FilterCourseBean filter : filters) {
            // LOGGER.info(filter.getUniversityName()); // Opzionale: rimuovere per pulizia se non essenziale
            collect(universityNames, filter.getUniversityName());
            collect(locations, filter.getUniversityLocation());
            collect(courseTypes, filter.getCourseType());
            collect(durations, filter.getCourseDurationRange());
            collect(rankings, filter.getUniversityRankingRange());
            collect(languages, filter.getLanguageOfInstruction());
        }

        // 3. Aggiornamento UI delegato
        updateComboBox(cmbUniversityName, universityNames);
        updateComboBox(cmbLocation, locations);
        updateComboBox(cmbCourseType, courseTypes);
        updateComboBox(cmbDuration, durations);
        updateComboBox(cmbRanking, rankings);
        updateComboBox(cmbLanguage, languages);
    }

// --- Metodi Ausiliari (Helper Methods) ---

    /**
     * Aggiunge il valore al set solo se non è nullo.
     */
    private void collect(Set<String> set, String value) {
        if (value != null) {
            set.add(value);
        }
    }

    /**
     * Gestisce la logica ripetitiva di pulizia e popolamento della ComboBox.
     */
    private void updateComboBox(ComboBox<String> comboBox, Set<String> items) {
        if (comboBox != null) {
            comboBox.getItems().clear();
            comboBox.getItems().add("");

            items.stream().sorted().forEach(comboBox.getItems()::add);

            comboBox.getItems().addAll(items);
        }
    }

    private void loadCourses(FilterCourseBean filter) {
        List<CourseBean> courses = courseController.searchCoursesByFilters(filter);
        displayCourses(courses);
    }

    private void displayCourses(List<CourseBean> courses) {
        if (courseListContainer == null) return;

        courseListContainer.getChildren().clear();

        if (courses == null || courses.isEmpty()) {
            Label noResults = new Label("No courses found. Try adjusting your search criteria.");
            noResults.setStyle("-fx-text-fill: #666666; -fx-font-size: 16;");
            courseListContainer.getChildren().add(noResults);
            return;
        }

        for (CourseBean course : courses) {
            HBox courseCard = createCourseCard(course);
            courseListContainer.getChildren().add(courseCard);
        }
    }

    private HBox createCourseCard(CourseBean course) {
        HBox card = new HBox();
        card.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 0); -fx-background-color: white; -fx-background-radius: 5; -fx-cursor: hand;");

        VBox infoVBox = new VBox(5); // Ho ridotto lo spacing da 10 a 5 per avvicinare titolo e università
        infoVBox.setStyle("-fx-padding: 20;");
        HBox.setHgrow(infoVBox, Priority.ALWAYS);

        // 1. Titolo del Corso
        Label titleLabel = new Label(course.getTitle() != null ? course.getTitle() : "Course Title");
        titleLabel.setFont(Font.font("System Bold", 20));

        // 2. [NUOVO] Nome dell'Università
        String uniName = "University info N/A";
        if (course.getUniversity() != null && course.getUniversity().getName() != null) {
            uniName = course.getUniversity().getName();
        }

        Label uniLabel = new Label(uniName);
        uniLabel.setFont(Font.font("System", 14)); // Font più piccolo
        uniLabel.setTextFill(Color.web("#666666")); // Colore grigio scuro per distinguerlo dal titolo

        // Spacer per spingere i tag in basso
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // 3. Tags
        HBox tagsBox = new HBox(10);
        if (course.getTags() != null) {
            for (String tag : course.getTags()) {
                Label tagLabel = new Label(tag);
                tagLabel.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 15; -fx-padding: 5 15;");
                tagLabel.setTextFill(Color.web("#555"));
                tagsBox.getChildren().add(tagLabel);
            }
        }

        // 4. Aggiunta dei componenti al VBox (Nota l'aggiunta di uniLabel)
        infoVBox.getChildren().addAll(titleLabel, uniLabel, spacer, tagsBox);

        // --- Parte destra della card (Colore e Freccia) ---
        Region colorRegion = new Region();
        colorRegion.setPrefWidth(10); // Ho ridotto la larghezza della banda colorata, esteticamente spesso è meglio
        colorRegion.setStyle("-fx-background-color: #ff9933; -fx-background-radius: 0 5 5 0;");
        // Nota: se vuoi mantenere lo stile originale "rettangolone" a destra, rimetti setPrefWidth(180)

        VBox arrowVBox = new VBox();
        arrowVBox.setAlignment(Pos.CENTER);
        arrowVBox.setPrefWidth(60);
        Label arrowLabel = new Label("->");
        arrowLabel.setFont(Font.font(24));
        arrowLabel.setTextFill(Color.web("#333"));
        arrowVBox.getChildren().add(arrowLabel);

        // Assemblaggio finale (Nota: ho rimosso colorRegion dal children se usi lo stile banda laterale sottile,
        // ma se vuoi mantenere il tuo layout originale con il blocco arancione grande a destra):

        // Layout originale tuo con blocco arancione:
        Region originalColorRegion = new Region();
        originalColorRegion.setPrefWidth(180);
        originalColorRegion.setStyle("-fx-background-color: #ff9933;");

        card.getChildren().addAll(infoVBox, originalColorRegion, arrowVBox);

        card.setOnMouseClicked(e -> onCourseSelected(course));

        return card;
    }

    @FXML
    public void onSearch(ActionEvent event) {
        String searchText = txtSearch != null ? txtSearch.getText() : "";

        if (searchText != null && !searchText.trim().isEmpty()) {
            CourseBean searchBean = new CourseBean();
            searchBean.setTitle(searchText.trim());
            searchBean.setCourseType(searchText.trim());
            List<CourseBean> results = courseController.searchCourseByName(searchBean);
            displayCourses(results);
        } else {
            loadCourses(null);
        }
    }

    @FXML
    public void onApplyFilters(ActionEvent event) {
        FilterCourseBean filter = new FilterCourseBean();

        if (cmbUniversityName != null && cmbUniversityName.getValue() != null && !cmbUniversityName.getValue().isEmpty()) {
            filter.setUniversityName(cmbUniversityName.getValue());
        }
        if (cmbLocation != null && cmbLocation.getValue() != null && !cmbLocation.getValue().isEmpty()) {
            filter.setUniversityLocation(cmbLocation.getValue());
        }
        if (cmbCourseType != null && cmbCourseType.getValue() != null && !cmbCourseType.getValue().isEmpty()) {
            filter.setCourseType(cmbCourseType.getValue());
        }
        if (cmbDuration != null && cmbDuration.getValue() != null && !cmbDuration.getValue().isEmpty()) {
            filter.setCourseDurationRange(cmbDuration.getValue());
        }
        if (cmbRanking != null && cmbRanking.getValue() != null && !cmbRanking.getValue().isEmpty()) {
            filter.setUniversityRankingRange(cmbRanking.getValue());
        }
        if (cmbLanguage != null && cmbLanguage.getValue() != null && !cmbLanguage.getValue().isEmpty()) {
            filter.setLanguageOfInstruction(cmbLanguage.getValue());
        }

        loadCourses(filter);
    }

    public void onCourseSelected(CourseBean course) {
        Navigator.getInstance().setCurrentData(course);
        ActionEvent event = new ActionEvent(btnHome, null);
        Navigator.getInstance().goTo(event, "CourseDetail.fxml");
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
