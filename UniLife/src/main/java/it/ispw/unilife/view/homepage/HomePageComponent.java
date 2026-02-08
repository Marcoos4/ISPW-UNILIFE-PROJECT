package it.ispw.unilife.view.homepage;

import javafx.scene.layout.VBox;

/**
 * Interface for the HomePage Decorator pattern.
 * Each component can add buttons to the home page based on user role.
 */
public interface HomePageComponent {

    /**
     * Decorate the content area with buttons specific to the user type.
     * @param contentArea The VBox to add buttons to
     */
    void decorateContent(VBox contentArea);

    /**
     * Decorate the header area (e.g., add logout/profile buttons).
     * @param headerArea The VBox/HBox to add header elements to
     */
    void decorateHeader(javafx.scene.layout.HBox headerArea);

    /**
     * Check if this component should show logout button.
     * @return true if logout should be shown
     */
    boolean showLogout();

    /**
     * Check if this component should show profile button.
     * @return true if profile should be shown
     */
    boolean showProfile();
}
