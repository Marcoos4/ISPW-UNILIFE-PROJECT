package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.LessonBean;
import it.ispw.unilife.bean.ReservationBean;
import it.ispw.unilife.controller.BookTutor;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.time.format.DateTimeFormatter;

public class TutorBookingFXController {

    @FXML private Button btnLogout;
    @FXML private Button btnProfile;
    @FXML private Button btnHome;

    @FXML private Button btnBackToTutor;
    @FXML private Button btnConferma;
    @FXML private Button btnLeaveReview;

    @FXML private Label lblLessonTime;
    @FXML private Label lblLessonSubject;
    @FXML private Label lblTutorDescription;
    @FXML private Label lblTutorInfo;
    @FXML private Label lblTutorName;
    @FXML private Label lblTutorRating;
    @FXML private ImageView imgTutor;

    private final BookTutor bookTutorController;
    private LessonBean currentLesson;
    private ReservationBean currentReservation;

    public TutorBookingFXController() {
        this.bookTutorController = new BookTutor();
    }

    @FXML
    public void initialize() {
        Object data = Navigator.getInstance().getCurrentData();
        currentReservation = (ReservationBean) data;
        if (data instanceof ReservationBean reservationBean) {
            currentLesson = (reservationBean).getLesson();
            populateLessonDetails(currentLesson);
        }
    }

    private void populateLessonDetails(LessonBean lesson) {
        if (lblLessonTime != null) {
            lblLessonTime.setText(formatLessonTime(lesson));
        }

        if (lblLessonSubject != null) {
            lblLessonSubject.setText(lesson.getSubject() != null ? lesson.getSubject() : "Lesson Subject");
        }

        if (lblTutorDescription != null && lesson.getTutor() != null) {
            lblTutorDescription.setText("Experienced tutor specializing in " + (lesson.getSubject() != null ? lesson.getSubject() : "various subjects") + ".");
        }

        if (lblTutorInfo != null) {
            StringBuilder info = new StringBuilder();
            info.append("Mode: Online/In Person\n");
            info.append("Duration: ").append(lesson.getDurationInHours()).append(" hours\n");
            info.append("Price: ").append(lesson.getPrice()).append(" EUR/hour");
            lblTutorInfo.setText(info.toString());
        }

        if (lblTutorName != null && lesson.getTutor() != null) {
            lblTutorName.setText("Prof. " + lesson.getTutor().getName() + " " + lesson.getTutor().getSurname());
        }

        if (lblTutorRating != null && lesson.getTutor() != null) {
            lblTutorRating.setText(String.format("%.1f/5", lesson.getTutor().getRating()));
        }
    }

    private String formatLessonTime(LessonBean lesson) {
        if (lesson.getStartTime() == null) {
            return "-- | -- | --";
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String date = lesson.getStartTime().format(dateFormatter);
        String time = lesson.getStartTime().format(timeFormatter);
        int duration = lesson.getDurationInHours();
        return String.format("%s | %s | %d hours", date, time, duration);
    }

    @FXML
    public void onBackToTutor(ActionEvent event) {
        Navigator.getInstance().goTo(event, "SearchTutor.fxml");
    }

    @FXML
    public void onConferma(ActionEvent event) {
        try {
            if (currentLesson != null && Navigator.getInstance().getCurrentToken() != null) {
                bookTutorController.confirmReservationProcedure(Navigator.getInstance().getCurrentToken(), currentReservation);
            }

            Navigator.getInstance().setCurrentData(currentLesson);
            Navigator.getInstance().goTo(event, "Home.fxml");
            showAlert(Alert.AlertType.INFORMATION, "Success", "Application submitted successfully!");
        }catch (DAOException e) {

            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Submission Failed", e.getMessage());
        } catch (UserNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "User not found", e.getMessage());
        }

    }

    @FXML
    public void onLeaveReview(ActionEvent event) {
        Navigator.getInstance().setCurrentData(currentLesson);
        Navigator.getInstance().goTo(event, "Review.fxml");
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
