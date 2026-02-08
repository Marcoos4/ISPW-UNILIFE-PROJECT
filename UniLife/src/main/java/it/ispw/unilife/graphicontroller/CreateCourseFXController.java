package it.ispw.unilife.graphicontroller;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.controller.ManageCourse;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.view.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class CreateCourseFXController {

    @FXML private TextField txtTitle;
    @FXML private TextField txtTags;
    @FXML private TextField txtLanguage;
    @FXML private TextField txtDuration;
    @FXML private TextField txtFees; // NUOVO CAMPO
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<String> cmbCourseType;
    @FXML private VBox reqContainer;

    private final ManageCourse appController = new ManageCourse();
    private static final String DOCUMENT = "DOCUMENT";

    @FXML
    public void initialize() {
        cmbCourseType.getItems().addAll("Undergraduate", "PostGraduate", "Phd");
    }

    @FXML
    public void onAddTextReq(ActionEvent event) {
        // ... codice esistente invariato ...
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-background-radius: 5;");
        row.setUserData("TEXT");

        TextField txtName = new TextField();
        txtName.setPromptText("Name");
        txtName.setPrefWidth(150);

        TextField txtDesc = new TextField();
        txtDesc.setPromptText("Description");
        txtDesc.setPrefWidth(200);

        TextField txtMin = new TextField();
        txtMin.setPromptText("Min");
        txtMin.setPrefWidth(50);

        TextField txtMax = new TextField();
        txtMax.setPromptText("Max");
        txtMax.setPrefWidth(50);

        Button btnRemove = new Button("X");
        btnRemove.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnRemove.setOnAction(e -> reqContainer.getChildren().remove(row));

        row.getChildren().addAll(new Label("TEXT:"), txtName, txtDesc, txtMin, txtMax, btnRemove);
        reqContainer.getChildren().add(row);
    }

    @FXML
    public void onAddDocReq(ActionEvent event) {
        // ... codice esistente invariato ...
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #e6f2ff; -fx-padding: 10; -fx-background-radius: 5;");
        row.setUserData(DOCUMENT);

        TextField txtName = new TextField();
        txtName.setPromptText("Doc Name");
        txtName.setPrefWidth(150);

        TextField txtDesc = new TextField();
        txtDesc.setPromptText("Description");
        txtDesc.setPrefWidth(180);

        TextField txtExt = new TextField();
        txtExt.setPromptText("Ext");
        txtExt.setPrefWidth(50);

        TextField txtSize = new TextField();
        txtSize.setPromptText("MB");
        txtSize.setPrefWidth(50);

        CheckBox chkCert = new CheckBox("Is Cert?");

        Button btnRemove = new Button("X");
        btnRemove.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        btnRemove.setOnAction(e -> reqContainer.getChildren().remove(row));

        row.getChildren().addAll(new Label("DOC:"), txtName, txtDesc, txtExt, txtSize, chkCert, btnRemove);
        reqContainer.getChildren().add(row);
    }

    @FXML
    public void onConfirm(ActionEvent event) {
        try {
            // 1. Validazione Campi Obbligatori
            validateMandatoryFields();

            // 2. Creazione Bean Base (Title, Desc, Lang, Type)
            CourseBean courseBean = initCourseBean();

            // 3. Parsing Campi Numerici e Liste (Gestione Errori incapsulata)
            courseBean.setDuration(parseDuration());
            courseBean.setFees(parseFees());
            courseBean.setTags(parseTags());

            // 4. Parsing Requirements (Logica complessa estratta)
            AdmissionRequirementBean admissionBean = new AdmissionRequirementBean();
            admissionBean.setRequirements(collectRequirementsFromUI());
            courseBean.setAdmissionRequirement(admissionBean);

            // 5. Invio al Controller
            TokenBean token = Navigator.getInstance().getCurrentToken();
            appController.addCourse(token, courseBean);

            showAlert("Course created successfully!");
            Navigator.getInstance().goToHome(event);

        } catch (IllegalArgumentException e) {
            // Cattura errori di validazione (Duration, Fees, Empty fields)
            showAlert(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error creating course: " + e.getMessage());
        }
    }

    // --- METODI HELPER DI ESTRAZIONE DATI ---

    private void validateMandatoryFields() {
        if (txtTitle.getText().isEmpty() || cmbCourseType.getValue() == null) {
            throw new IllegalArgumentException("Title and Course Type are mandatory.");
        }
    }

    private CourseBean initCourseBean() {
        CourseBean bean = new CourseBean();
        bean.setTitle(txtTitle.getText());
        bean.setDescription(txtDescription.getText());
        bean.setLanguageOfInstruction(txtLanguage.getText());
        bean.setCourseType(cmbCourseType.getValue());
        return bean;
    }

    private int parseDuration() {
        try {
            return Integer.parseInt(txtDuration.getText());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Duration must be a valid integer number.");
        }
    }

    private double parseFees() {
        String feesStr = txtFees.getText();
        if (feesStr == null || feesStr.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(feesStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Fees must be a valid numeric value (e.g., 1500.50).");
        }
    }

    private List<String> parseTags() {
        List<String> tags = new ArrayList<>();
        if (txtTags.getText() != null && !txtTags.getText().isEmpty()) {
            String[] rawTags = txtTags.getText().split(",");
            for (String t : rawTags) {
                // Assumo che CourseTags sia un enum
                CourseTags validTag = CourseTags.fromString(t.trim());
                if (validTag != null) tags.add(validTag.name());
            }
        }
        return tags;
    }

    // --- GESTIONE REQUISITI (La parte più complessa) ---

    private List<RequirementBean> collectRequirementsFromUI() {
        List<RequirementBean> reqList = new ArrayList<>();

        for (Node node : reqContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                // Assumo che lo UserData sia stato settato durante la creazione della riga
                String type = (String) row.getUserData();

                RequirementBean req = null;
                if ("TEXT".equals(type)) {
                    req = extractTextRequirement(row);
                } else if (DOCUMENT.equals(type)) { // O usa la costante DOCUMENT se definita
                    req = extractDocRequirement(row);
                }

                if (req != null) {
                    reqList.add(req);
                }
            }
        }
        return reqList;
    }

    /**
     * Estrae i dati da una riga HBox di tipo TEXT.
     * Nota: Mantiene gli indici originali (getChildren().get(X))
     */
    private RequirementBean extractTextRequirement(HBox row) {
        RequirementBean req = new RequirementBean();
        req.setType("TEXT");

        // Attenzione agli indici: devono corrispondere a come hai costruito la UI
        TextField txtLabel = (TextField) row.getChildren().get(1);
        TextField txtDesc = (TextField) row.getChildren().get(2);
        TextField txtMin = (TextField) row.getChildren().get(3);
        TextField txtMax = (TextField) row.getChildren().get(4);

        req.setLabel(txtLabel.getText());
        req.setName(req.getLabel().replaceAll("\\s+", "_").toLowerCase());
        req.setDescription(txtDesc.getText());

        try {
            req.setMinChars(Integer.parseInt(txtMin.getText()));
            req.setMaxChars(Integer.parseInt(txtMax.getText()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Char limits must be numbers for requirement: " + req.getLabel());
        }
        return req;
    }

    /**
     * Estrae i dati da una riga HBox di tipo DOCUMENT.
     */
    private RequirementBean extractDocRequirement(HBox row) {
        RequirementBean req = new RequirementBean();
        req.setType(DOCUMENT);

        TextField txtLabel = (TextField) row.getChildren().get(1);
        TextField txtDesc = (TextField) row.getChildren().get(2);
        TextField txtExt = (TextField) row.getChildren().get(3);
        TextField txtSize = (TextField) row.getChildren().get(4);
        CheckBox chkCert = (CheckBox) row.getChildren().get(5);

        req.setLabel(txtLabel.getText());
        req.setName(req.getLabel().replaceAll("\\s+", "_").toLowerCase());
        req.setDescription(txtDesc.getText());
        req.setAllowedExtension(txtExt.getText());
        req.setCertificate(chkCert.isSelected());

        try {
            req.setMaxSizeMB(Double.parseDouble(txtSize.getText()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Size must be a number for requirement: " + req.getLabel());
        }
        return req;
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML public void onCancel(ActionEvent event) { Navigator.getInstance().goToHome(event); }
    @FXML public void onLogout(ActionEvent event) { Navigator.getInstance().logout(event); }
    @FXML public void onHome(ActionEvent event) { Navigator.getInstance().goToHome(event); }
}