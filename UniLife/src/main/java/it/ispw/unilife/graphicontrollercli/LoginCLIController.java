package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.bean.UserBean;
import it.ispw.unilife.controller.LoginController;
import it.ispw.unilife.exception.ExternalAuthenticationException;
import it.ispw.unilife.exception.LoginException;
import it.ispw.unilife.view.viewcli.LoginCLIView;

import java.util.Scanner;

public class LoginCLIController implements CLIContoller {
    LoginController loginController = new LoginController();
    private final LoginCLIView view = new LoginCLIView();

    TokenBean tokenBean = new TokenBean();

    @Override
    public void start(Scanner scanner) {
        boolean isRunning = true;

        while (isRunning) {
            view.showOptions();
            String input = scanner.nextLine();

            // Caso speciale per l'uscita immediata
            if ("0".equals(input)) {
                System.exit(0);
            }

            // 1. Tenta di ottenere un token basato sull'input
            // Se l'input è 4 (Home) o non valido, o se il login fallisce, restituisce null.
            TokenBean token = processInputAndGetToken(input, scanner);

            // 2. Se abbiamo un token valido, procediamo con il routing
            if (token != null) {
                this.tokenBean = token; // Aggiorna lo stato della classe se necessario
                view.showSuccess();

                // Se il routing fallisce (ruolo non trovato), fermiamo il loop come da codice originale
                boolean roleHandled = routeUserByRole(token, scanner);
                if (!roleHandled) {
                    isRunning = false;
                }
            }
        }
    }

    private TokenBean processInputAndGetToken(String input, Scanner scanner) {
        try {
            switch (input) {
                case "1":
                    return performCredentialLogin(scanner);
                case "2":
                    new HomeCLIController().start(scanner);
                    return null;
                default:
                    view.showError("Invalid input");
                    return null;
            }
        } catch ( ExternalAuthenticationException e) {
            view.showError(e.getMessage());
            return null;
        } catch (LoginException e) {
            System.out.println("User not found");
        }
        return null;
    }

    // Metodo helper per il login classico
    private TokenBean performCredentialLogin(Scanner scanner) throws LoginException {
        view.promptUsername();
        String user = scanner.nextLine();
        view.promptPassword();
        String pass = scanner.nextLine();

        UserBean bean = new UserBean();
        bean.setUserName(user);
        bean.setPassword(pass);

        return loginController.login(bean);
    }

    // Estrazione della logica di routing
    private boolean routeUserByRole(TokenBean token, Scanner scanner) {
        UserBean loggedBean = loginController.findUserRole(token);
        String role = loggedBean.getRole();

        switch (role) {
            case "Student":
                new StudentHomeCLIController(token).start(scanner);
                return true;
            case "Tutor":
                new TutorHomeCLIController(token).start(scanner);
                return true;
            case "University_employee":
                new EmployeeHomeCLIController(token).start(scanner);
                return true;
            default:
                return false; // Segnala che il ruolo non è gestito
        }
    }
}
