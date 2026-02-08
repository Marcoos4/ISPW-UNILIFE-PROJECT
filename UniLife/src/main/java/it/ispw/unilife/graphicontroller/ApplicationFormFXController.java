package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.controller.CourseDiscoveryAndApplication;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.InvalidCertificateException;
import it.ispw.unilife.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationFormFXController {

    @FXML private Label lblCourseTitle;
    @FXML private VBox textRequirementsContainer;
    @FXML private VBox docRequirementsContainer;

    @FXML private Button btnLogout;
    @FXML private Button btnProfile;
    @FXML private Button btnHome;
    @FXML private Button btnBack;

    private CourseBean selectedCourse;
    private final CourseDiscoveryAndApplication appController = new CourseDiscoveryAndApplication();
    private static final Logger LOGGER = Logger.getLogger(ApplicationFormFXController.class.getName());

    private final Map<String, TextArea> textInputMap = new HashMap<>();
    private final Map<String, File> fileInputMap = new HashMap<>();

    @FXML
    public void initialize() {
        Object data = Navigator.getInstance().getCurrentData();
        if (data instanceof CourseBean) {
            this.selectedCourse = (CourseBean) data;
            lblCourseTitle.setText(selectedCourse.getTitle() + " - Application");
            loadRequirements();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No course selected.");
            onHome(null);
        }
    }

    private void loadRequirements() {
        AdmissionRequirementBean reqBean = null;
        try {
            reqBean = appController.findCourseAdmissionRequirements(selectedCourse);
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        if (reqBean == null || reqBean.getRequirements() == null) {
            Label lblNoReq = new Label("No specific requirements found for this course.");
            textRequirementsContainer.getChildren().add(lblNoReq);
            return;
        }

        for (RequirementBean req : reqBean.getRequirements()) {
            if ("TEXT".equalsIgnoreCase(req.getType())) {
                createTextField(req);
            } else if ("DOCUMENT".equalsIgnoreCase(req.getType())) {
                createDocField(req);
            }
        }
    }

    private void createTextField(RequirementBean req) {
        Label label = new Label(req.getLabel());
        label.setFont(new Font("System Bold", 14));
        Label desc = new Label(req.getDescription());
        desc.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");

        TextArea textArea = new TextArea();
        textArea.setPromptText("Min chars: " + req.getMinChars() + " - Max chars: " + req.getMaxChars());
        textArea.setWrapText(true);
        textArea.setPrefHeight(150);
        textArea.setStyle("-fx-background-color: #f5f5f5;");

        textInputMap.put(req.getName(), textArea);
        textRequirementsContainer.getChildren().addAll(label, desc, textArea);
    }

    private void createDocField(RequirementBean req) {
        VBox docBox = new VBox(5);
        Label label = new Label(req.getLabel());
        label.setFont(new Font("System Bold", 12));
        String extensions = (req.getAllowedExtension() != null) ? req.getAllowedExtension() : "*.*";
        Label infoLabel = new Label("Max " + req.getMaxSizeMB() + "MB (" + extensions + ")");
        infoLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 10px;");

        Button uploadBtn = new Button("+ Upload");
        uploadBtn.setStyle("-fx-background-color: #ff9933; -fx-text-fill: white; -fx-background-radius: 20;");
        Label selectedFileLabel = new Label("No file selected");
        selectedFileLabel.setStyle("-fx-font-style: italic;");

        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select " + req.getLabel());
            if (req.getAllowedExtension() != null && !req.getAllowedExtension().isEmpty()) {
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter(req.getAllowedExtension().toUpperCase() + " Files", "*." + req.getAllowedExtension())
                );
            }
            File file = fileChooser.showOpenDialog(uploadBtn.getScene().getWindow());
            if (file != null) {
                double megabytes = file.length() / (1024.0 * 1024.0);
                if (megabytes > req.getMaxSizeMB()) {
                    selectedFileLabel.setText("File too large! (Max " + req.getMaxSizeMB() + "MB)");
                    selectedFileLabel.setStyle("-fx-text-fill: red;");
                    fileInputMap.remove(req.getName());
                } else {
                    selectedFileLabel.setText(file.getName());
                    selectedFileLabel.setStyle("-fx-text-fill: green;");
                    fileInputMap.put(req.getName(), file);
                }
            }
        });

        docBox.getChildren().addAll(label, infoLabel, uploadBtn, selectedFileLabel);
        docRequirementsContainer.getChildren().add(docBox);
    }

    @FXML
    public void onSubmit(ActionEvent event) {
        try {
            // 1. Costruzione dell'ApplicationBean
            ApplicationBean appBean = new ApplicationBean();
            appBean.setCourseBean(this.selectedCourse);

            List<ApplicationItemBean> items = new ArrayList<>();

            // 2. Raccolta dati Testuali
            for (Map.Entry<String, TextArea> entry : textInputMap.entrySet()) {
                ApplicationItemBean item = new ApplicationItemBean();
                item.setRequirementName(entry.getKey());
                item.setType("TEXT");
                item.setTextContent(entry.getValue().getText());
                items.add(item);
            }

            // 3. Raccolta dati Documentali
            for (Map.Entry<String, File> entry : fileInputMap.entrySet()) {
                ApplicationItemBean item = new ApplicationItemBean();
                item.setRequirementName(entry.getKey());
                item.setType("DOCUMENT");

                DocumentBean docBean = fileToDocumentBean(entry.getValue());
                item.setDocument(docBean);
                items.add(item);
            }

            appBean.setItems(items);

            // 4. Invio al controller
            TokenBean token = Navigator.getInstance().getCurrentToken();
            if (token == null) {
                showAlert(Alert.AlertType.ERROR, "Session Error", "You are not logged in.");
                return;
            }

            appController.submitApplication(token, appBean);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Application submitted successfully!");
            Navigator.getInstance().goToHome(event);
        } catch (InvalidCertificateException e ) {
            showAlert(Alert.AlertType.ERROR, "The Certification Inserted is not valid", e.getMessage());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Upload Error", "Error reading file");
        }catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Submission Failed", e.getMessage());
        }
    }

    @FXML
    public void onSave(ActionEvent event) {
        LOGGER.info("Saving application...");
    }

    private DocumentBean fileToDocumentBean(File file) throws IOException {
        DocumentBean docBean = new DocumentBean();
        docBean.setFileName(file.getName());

        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");

        if (lastIndexOf > 0 && lastIndexOf < name.length() - 1) {
            String ext = name.substring(lastIndexOf + 1).toUpperCase();
            docBean.setFileType(ext);
        } else {
            docBean.setFileType("");
        }

        docBean.setFileSize(file.length() / 1024.0); // KB

        byte[] fileContent = Files.readAllBytes(file.toPath());
        docBean.setContent(fileContent);

        return docBean;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void onBack(ActionEvent event) {
        Navigator.getInstance().setCurrentData(selectedCourse);
        Navigator.getInstance().goTo(event, "CourseDetail.fxml");
    }

    @FXML
    public void onLogout(ActionEvent event) {
        Navigator.getInstance().logout(event);
    }

    @FXML
    public void onHome(ActionEvent event) {
        Navigator.getInstance().goToHome(event);
    }

    @FXML
    public void onRequestTutoring(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}