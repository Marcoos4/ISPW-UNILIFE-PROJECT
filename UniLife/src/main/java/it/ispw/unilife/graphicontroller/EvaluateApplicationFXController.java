package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.controller.CourseDiscoveryAndApplication;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EvaluateApplicationFXController {

    private static final Logger logger = Logger.getLogger(EvaluateApplicationFXController.class.getName());

    @FXML private Label lblStudentName;
    @FXML private Label lblCourseTitle;
    @FXML private Label lblSubmissionDate;
    @FXML private VBox itemsContainer;

    TokenBean tokenBean;
    private ApplicationBean currentApplication;
    private final CourseDiscoveryAndApplication manageApplicationController = new CourseDiscoveryAndApplication();

    @FXML
    public void initialize() {
        Object data = Navigator.getInstance().getCurrentData();
        tokenBean = Navigator.getInstance().getCurrentToken();

        if (data instanceof EvaluationContextBean evaluationContextBean) {
            EvaluationContextBean context = evaluationContextBean;
            this.currentApplication = context.getApplicationBean();

            populateView();
        }

        else if (data instanceof ApplicationBean applicationBean) {
            this.currentApplication = applicationBean;
            populateView();
        } else {
            logger.severe("Nessun dato valido trovato nel Navigator.");
            showAlert("Errore", "Impossibile caricare i dati.");
        }
    }

    private void populateView() {
        if (currentApplication == null) return;

        lblStudentName.setText(currentApplication.getStudentName().getName() + " " + currentApplication.getStudentName().getSurname());
        lblCourseTitle.setText(currentApplication.getCourseBean().getTitle());
        lblSubmissionDate.setText("Sottomessa il: " + currentApplication.getSubmissionDate());

        itemsContainer.getChildren().clear();

        if (currentApplication.getItems() != null) {
            for (ApplicationItemBean item : currentApplication.getItems()) {
                VBox itemBox = createItemCard(item);
                itemsContainer.getChildren().add(itemBox);
            }
        }
    }

    private VBox createItemCard(ApplicationItemBean item) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label nameLbl = new Label(item.getRequirementName());
        nameLbl.setFont(Font.font("System", FontWeight.BOLD, 14));

        card.getChildren().add(nameLbl);

        if ("TEXT".equalsIgnoreCase(item.getType())) {
            TextArea textArea = new TextArea(item.getTextContent());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefHeight(80);
            textArea.setStyle("-fx-background-color: #f9f9f9;");
            card.getChildren().add(textArea);

        } else if ("DOCUMENT".equalsIgnoreCase(item.getType())) {
            DocumentBean doc = item.getDocument();
            HBox docBox = new HBox(10);
            docBox.setStyle("-fx-background-color: #f0f8ff; -fx-padding: 10; -fx-background-radius: 5;");

            SVGPath icon = new SVGPath();
            icon.setContent("M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z");
            icon.setFill(Color.web("#3b6e8a"));

            String fileName = (doc != null && doc.getFileName() != null) ? doc.getFileName() : "Documento allegato";
            Label fileLbl = new Label(fileName);
            fileLbl.setFont(Font.font("System", FontWeight.NORMAL, 13));

            Button downloadBtn = new Button("Download / View");
            downloadBtn.setStyle("-fx-background-color: #3b6e8a; -fx-text-fill: white; -fx-font-size: 11px;");
            downloadBtn.setOnAction(e -> showAlert("Download", "Scaricamento del file: " + fileName));

            docBox.getChildren().addAll(icon, fileLbl, downloadBtn);
            card.getChildren().add(docBox);
        }

        return card;
    }

    @FXML
    public void onAccept(ActionEvent event) {
        currentApplication.setStatus("ACCEPTED");
        submitEvaluation(event);
    }

    @FXML
    public void onReject(ActionEvent event) {
        currentApplication.setStatus("REJECTED");
        submitEvaluation(event);
    }

    private void submitEvaluation(ActionEvent event) {
        try {
            manageApplicationController.evaluateApplication(tokenBean, currentApplication);

            showAlert("Success", "Registered: " + currentApplication.getStatus());
            Navigator.getInstance().goToHome(event);
        } catch (UserNotFoundException e){
            showAlert("Error.", "User not logged in.");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Errore nella valutazione", e);
            showAlert("Errore", "Impossibile completare l'operazione.");
        }
    }

    @FXML
    public void onBack(ActionEvent event) {
        Navigator.getInstance().goTo(event, "Profile.fxml"); // O torna indietro semplicemente
    }

    @FXML
    public void onLogout(ActionEvent event) {
        Navigator.getInstance().logout(event);
    }

    @FXML
    public void onHome(ActionEvent event) {
        Navigator.getInstance().goToHome(event);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}