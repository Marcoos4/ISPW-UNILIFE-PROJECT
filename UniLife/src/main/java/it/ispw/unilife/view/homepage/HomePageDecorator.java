package it.ispw.unilife.view.homepage;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public abstract class HomePageDecorator implements HomePageComponent {

    protected HomePageComponent wrappedComponent;

    protected HomePageDecorator(HomePageComponent component) {
        this.wrappedComponent = component;
    }

    @Override
    public void decorateContent(VBox contentArea) {
        wrappedComponent.decorateContent(contentArea);
    }

    @Override
    public void decorateHeader(HBox headerArea) {
        wrappedComponent.decorateHeader(headerArea);
    }

    @Override
    public boolean showLogout() {
        return wrappedComponent.showLogout();
    }

    @Override
    public boolean showProfile() {
        return wrappedComponent.showProfile();
    }
}
