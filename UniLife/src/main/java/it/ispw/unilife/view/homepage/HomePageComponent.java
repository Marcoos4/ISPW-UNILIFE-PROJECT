package it.ispw.unilife.view.homepage;

import javafx.scene.layout.VBox;


public interface HomePageComponent {


    void decorateContent(VBox contentArea);


    void decorateHeader(javafx.scene.layout.HBox headerArea);


    boolean showLogout();


    boolean showProfile();
}
