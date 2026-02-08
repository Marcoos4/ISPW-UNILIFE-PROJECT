package it.ispw.unilife.view.homepage;

import it.ispw.unilife.view.Navigator;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Base (Guest) home page component.
 * Shows only Login/Register button.
 */
public class BaseHomePageComponent implements HomePageComponent {

    private static final String BUTTON_STYLE = "-fx-background-color: #ff9933; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold;";

    @Override
    public void decorateContent(VBox contentArea) {
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setSpacing(30);
        contentArea.getChildren().clear();

        Button btnLoginRegister = createStyledButton("Login/Register", 280, 55);
        btnLoginRegister.setOnAction(event -> Navigator.getInstance().goTo(event, "Login.fxml"));

        contentArea.getChildren().add(btnLoginRegister);
    }

    @Override
    public void decorateHeader(HBox headerArea) {
        // Guest doesn't need logout or profile buttons
    }

    @Override
    public boolean showLogout() {
        return false;
    }

    @Override
    public boolean showProfile() {
        return false;
    }

    protected Button createStyledButton(String text, double width, double height) {
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
