package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.FilterTutorBean;
import it.ispw.unilife.bean.LessonBean;
import it.ispw.unilife.bean.ReservationBean;
import it.ispw.unilife.controller.BookTutor;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.InvalidTokenException;
import it.ispw.unilife.view.Navigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchTutorFXController {

    private static final Logger LOGGER = Logger.getLogger(SearchTutorFXController.class.getName());
    private static final String SYSTEMBOLD = "System Bold";

    @FXML private Button btnLogout;
    @FXML private Button btnProfile;
    @FXML private Button btnHome;
    @FXML private Button btnApplyFilters;

    // MODIFICA: ComboBox invece di TextField
    @FXML private ComboBox<String> cmbSubject;
    @FXML private DatePicker pickerDate;

    @FXML private VBox tutorListContainer;

    private final BookTutor bookTutorController;

    public SearchTutorFXController() {
        this.bookTutorController = new BookTutor();
    }

    @FXML
    public void initialize() {
        // 1. Popola la ComboBox con i dati dal Controller Applicativo
        loadFilterOptions();

        // 2. Carica tutte le lezioni disponibili all'avvio (Filtro vuoto)
        loadAvailableLessons(new FilterTutorBean());
    }

    private void loadFilterOptions() {
        List<String> subjects = bookTutorController.getAvailableSubjects();

        // Aggiungiamo un'opzione vuota o gestiamo la deselezione tramite il tasto Clear (opzionale)
        // Qui carichiamo semplicemente le materie trovate
        ObservableList<String> subjectList = FXCollections.observableArrayList(subjects);
        cmbSubject.setItems(subjectList);
    }

    @FXML
    public void onApplyFilters(ActionEvent event) {
        FilterTutorBean filter = new FilterTutorBean();

        // 1. Mappatura Subject (Lettura da ComboBox)
        String selectedSubject = cmbSubject.getValue();
        if (selectedSubject != null && !selectedSubject.trim().isEmpty()) {
            filter.setSubject(selectedSubject);
        }

        filter.setAmount(0);

        // 3. Mappatura Date (Start & End)
        if (pickerDate.getValue() != null) {
            LocalDate selectedDate = pickerDate.getValue();
            // Imposta start alle 00:00 e end alle 23:59:59
            LocalDateTime startDateTime = selectedDate.atStartOfDay();
            LocalDateTime endDateTime = selectedDate.atTime(LocalTime.MAX);

            filter.setStart(startDateTime);
            filter.setEnd(endDateTime);
        }

        // Esegui la ricerca con i parametri popolati
        loadAvailableLessons(filter);
    }

    private void loadAvailableLessons(FilterTutorBean filter) {
        try {
            List<LessonBean> lessons = bookTutorController.filterTutor(filter);
            displayLessons(lessons);
        } catch (DAOException e) {
            LOGGER.log(Level.WARNING, "Error loading lessons", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Errore di connessione al database.", ButtonType.OK);
            alert.show();
        }
    }

    private void displayLessons(List<LessonBean> lessons) {
        if (tutorListContainer == null) return;

        tutorListContainer.getChildren().clear();

        // Header Title
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        VBox titleVBox = new VBox();
        Label titleLabel = new Label("Tutor Available");
        titleLabel.setFont(Font.font(SYSTEMBOLD, 24));
        Rectangle underline = new Rectangle(60, 3);
        underline.setFill(Color.web("#333333"));
        titleVBox.getChildren().addAll(titleLabel, underline);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleBox.getChildren().addAll(titleVBox, spacer);

        tutorListContainer.getChildren().add(titleBox);

        if (lessons == null || lessons.isEmpty()) {
            Label noResultsLabel = new Label("No lessons match your filters.");
            noResultsLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 16; -fx-padding: 20 0 0 0;");
            tutorListContainer.getChildren().add(noResultsLabel);
            return;
        }

        for (LessonBean lesson : lessons) {
            HBox lessonCard = createLessonCard(lesson);
            tutorListContainer.getChildren().add(lessonCard);
        }
    }

    private HBox createLessonCard(LessonBean lesson) {
        HBox card = new HBox(10);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 0); -fx-padding: 20; -fx-background-radius: 5; -fx-cursor: hand;");

        VBox infoVBox = new VBox(5);
        HBox.setHgrow(infoVBox, Priority.ALWAYS);

        // Time Info
        String timeInfo = formatLessonTime(lesson);
        Label timeLabel = new Label(timeInfo);
        timeLabel.setStyle("-fx-text-fill: #ff9933; -fx-font-weight: bold;");
        timeLabel.setFont(Font.font(SYSTEMBOLD, 14));

        // Subject
        Label subjectLabel = new Label(lesson.getSubject() != null ? lesson.getSubject() : "Subject Unavailable");
        subjectLabel.setFont(Font.font(SYSTEMBOLD, 20));

        // Tutor Name & Price
        String tutorName = lesson.getTutor() != null ?
                "Prof. " + lesson.getTutor().getName() + " " + lesson.getTutor().getSurname() : "Tutor";
        Label tutorLabel = new Label(tutorName + "  •  €" + lesson.getPrice());
        tutorLabel.setStyle("-fx-text-fill: #666666;");

        infoVBox.getChildren().addAll(timeLabel, subjectLabel, tutorLabel);

        // Rating Section
        VBox ratingVBox = new VBox(10);
        ratingVBox.setAlignment(Pos.TOP_RIGHT);

        // Placeholder stars
        Label ratingLabel = new Label("⭐ " + (lesson.getTutor() != null ? String.format("%.1f", lesson.getTutor().getRating()) : "-"));
        ratingLabel.setFont(Font.font(SYSTEMBOLD, 14));

        ratingVBox.getChildren().add(ratingLabel);

        card.getChildren().addAll(infoVBox, ratingVBox);

        card.setOnMouseClicked(e -> onTutorSelected(lesson));

        return card;
    }

    private String formatLessonTime(LessonBean lesson) {
        if (lesson.getStartTime() == null) return "--";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return lesson.getStartTime().format(df);
    }

    public void onTutorSelected(LessonBean lesson) {
        try {
            ReservationBean reservationBean = bookTutorController.startReservationProcedure(Navigator.getInstance().getCurrentToken(), lesson);
            Navigator.getInstance().setCurrentData(reservationBean);
            ActionEvent event = new ActionEvent(btnHome, null);
            Navigator.getInstance().goTo(event, "TutorBooking.fxml");
        } catch (InvalidTokenException e) {
            LOGGER.severe("Session expired");
            Navigator.getInstance().logout(new ActionEvent());
        }
    }

    @FXML public void onLogout(ActionEvent event) { Navigator.getInstance().logout(event); }
    @FXML public void onProfile(ActionEvent event) { Navigator.getInstance().goTo(event, "Profile.fxml"); }
    @FXML public void onHome(ActionEvent event) { Navigator.getInstance().goToHome(event); }
}