package it.ispw.unilife.view.homepage;

import it.ispw.unilife.view.Navigator;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class EmployeeHomeDecorator extends HomePageDecorator {

    private static final String BUTTON_STYLE = "-fx-background-color: #ff9933; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold;";

    public EmployeeHomeDecorator(HomePageComponent component) {
        super(component);
    }

    @Override
    public void decorateContent(VBox contentArea) {
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setSpacing(30);
        contentArea.getChildren().clear();

        Button btnAddCourse = createStyledButton("Add Course", 280, 55);
        btnAddCourse.setOnAction(event -> Navigator.getInstance().goTo(event, "CreateCourse.fxml"));

        Button btnShowNotifications = createStyledButton("Show Notifications", 280, 55);
        btnShowNotifications.setOnAction(event -> Navigator.getInstance().goTo(event, "Profile.fxml"));

        contentArea.getChildren().addAll(btnAddCourse, btnShowNotifications);
    }

    @Override
    public void decorateHeader(HBox headerArea) {
        // Header decoration is handled by HomeFXController
    }

    @Override
    public boolean showLogout() {
        return true;
    }

    @Override
    public boolean showProfile() {
        return true;
    }

    private Button createStyledButton(String text, double width, double height) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        button.setStyle(BUTTON_STYLE);
        button.setFont(new Font(18));

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(3);
        shadow.setRadius(5);
        shadow.setColor(Color.color(0, 0, 0, 0.25));
        button.setEffect(shadow);

        return button;
    }
}
