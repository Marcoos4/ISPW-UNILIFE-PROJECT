package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.bean.UniversityBean;
import it.ispw.unilife.bean.UserBean;
import it.ispw.unilife.controller.LoginController;
import it.ispw.unilife.enums.Role;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.RegistrationException;
import it.ispw.unilife.view.viewcli.RegisterCLIView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RegisterCLIController implements CLIContoller {

    // Costanti per evitare la duplicazione delle stringhe (SonarQube)
    private static final String ROLE_STUDENT = "Student";
    private static final String ROLE_TUTOR = "Tutor";
    private static final String ROLE_EMPLOYEE = "University Employee";

    private final RegisterCLIView view = new RegisterCLIView();
    private final LoginController loginController = new LoginController();

    @Override
    public void start(Scanner scanner) {
        view.showOptions();

        // Raccolta Input
        view.promptUsername();
        String username = scanner.nextLine().trim();

        view.promptPassword();
        String password = scanner.nextLine().trim();

        view.promptName();
        String name = scanner.nextLine().trim();

        view.promptSurname();
        String surname = scanner.nextLine().trim();

        // Selezione Ruolo
        String roleStr = selectRole(scanner);
        if (roleStr == null) return;

        // Selezione Università (solo se Employee)
        String university = null;
        if (ROLE_EMPLOYEE.equals(roleStr)) {
            try {
                university = selectUniversity(scanner);
            } catch (DAOException e) {
                view.showError("Select a university");
            }
            if (university == null) return;
        }

        // Validazione
        if (!validateInputs(username, password, name, surname, roleStr, university)) {
            return;
        }

        // Creazione e popolamento del Bean tramite SETTER
        UserBean userBean = new UserBean();
        userBean.setUserName(username);
        userBean.setPassword(password);
        userBean.setName(name);
        userBean.setSurname(surname);

        // Impostiamo l'università solo se presente (o null se non richiesta)
        if (university != null) {
            userBean.setUniversity(university);
        }

        // Procedi con la registrazione
        processRegistration(scanner, userBean, roleStr);
    }

    private String selectRole(Scanner scanner) {
        view.showRoleMenu();
        String roleInput = scanner.nextLine().trim();
        switch (roleInput) {
            case "1": return ROLE_STUDENT;
            case "2": return ROLE_TUTOR;
            case "3": return ROLE_EMPLOYEE;
            default:
                view.showError("Invalid role");
                return null;
        }
    }

    private String selectUniversity(Scanner scanner) throws DAOException {
        List<UniversityBean> uniBeans = loginController.findAvailableUniversities();
        List<String> uniNames = new ArrayList<>();

        if (uniBeans != null) {
            for (UniversityBean bean : uniBeans) {
                uniNames.add(bean.getName());
            }
        }

        if (uniNames.isEmpty()) {
            view.showError("No universities available.");
            return null;
        }

        view.showUniversityMenu(uniNames);
        try {
            String input = scanner.nextLine().trim();
            int uniChoice = Integer.parseInt(input);
            if (uniChoice < 1 || uniChoice > uniNames.size()) {
                view.showError("Invalid choice");
                return null;
            }
            return uniNames.get(uniChoice - 1);
        } catch (NumberFormatException e) {
            view.showError("Invalid input");
            return null;
        }
    }

    private boolean validateInputs(String username, String password, String name, String surname, String roleStr, String university) {
        if (username.isEmpty()) { view.showError("Username is required"); return false; }
        if (password.isEmpty()) { view.showError("Password is required"); return false; }
        if (name.isEmpty()) { view.showError("Name is required"); return false; }
        if (surname.isEmpty()) { view.showError("Surname is required"); return false; }

        if (ROLE_EMPLOYEE.equals(roleStr) && (university == null || university.isEmpty())) {
            view.showError("Select a University");
            return false;
        }
        return true;
    }

    private void processRegistration(Scanner scanner, UserBean userBean, String roleStr) {
        try {
            // Conversione e set del ruolo nel bean
            Role role = convertStringToRole(roleStr);
            userBean.setRole(role.name());

            // Chiamata al controller applicativo
            TokenBean tokenBean = loginController.register(userBean);

            view.showSuccess();
            routeToHome(scanner, roleStr, tokenBean);

        } catch (RegistrationException e) {
            view.showError(e.getMessage());
        }
    }

    private void routeToHome(Scanner scanner, String roleStr, TokenBean tokenBean) {
        switch (roleStr) {
            case ROLE_STUDENT:
                new StudentHomeCLIController(tokenBean).start(scanner);
                break;
            case ROLE_TUTOR:
                new TutorHomeCLIController(tokenBean).start(scanner);
                break;
            case ROLE_EMPLOYEE:
                new EmployeeHomeCLIController(tokenBean).start(scanner);
                break;
            default:
                break;
        }
    }

    private Role convertStringToRole(String roleStr) {
        switch (roleStr) {
            case ROLE_TUTOR: return Role.TUTOR;
            case ROLE_EMPLOYEE: return Role.UNIVERSITY_EMPLOYEE;
            default: return Role.STUDENT;
        }
    }
}