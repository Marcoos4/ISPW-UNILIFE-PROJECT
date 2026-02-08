package it.ispw.unilife.view.homepage;

import it.ispw.unilife.view.Navigator;

/**
 * Factory for creating the appropriate HomePage decorator based on user role.
 */
public class HomePageFactory {

    private HomePageFactory() {
    }

    /**
     * Create the appropriate HomePage component based on the current user's role.
     * @return The decorated HomePage component
     */
    public static HomePageComponent createHomePage() {
        HomePageComponent base = new BaseHomePageComponent();

        if (!Navigator.getInstance().isLoggedIn()) {
            return base;
        }

        String role = Navigator.getInstance().getCurrentUserRole();
        if (role == null) {
            return base;
        }

        switch (role) {
            case "Student":
                return new StudentHomeDecorator(base);
            case "Tutor":
                return new TutorHomeDecorator(base);
            case "University_employee":
                return new EmployeeHomeDecorator(base);
            default:
                return base;
        }
    }
}
