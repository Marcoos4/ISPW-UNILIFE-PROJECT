package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.view.viewcli.StudentHomeCLIView;

import java.util.Scanner;

public class StudentHomeCLIController implements CLIContoller {
    private final StudentHomeCLIView view = new StudentHomeCLIView();
    private final TokenBean tokenBean;

    public StudentHomeCLIController(TokenBean tokenBean) {
        this.tokenBean = tokenBean;
    }

    @Override
    public void start(Scanner scanner) {
        boolean flag = true;

        while (flag) {
            view.showMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    new SearchCourseCLIController(tokenBean).start(scanner);
                    break;
                case "2":
                    new SearchTutorCLIController(tokenBean).start(scanner);
                    break;
                case "3":
                    new ProfileCLIController(tokenBean).start(scanner);
                    break;
                case "4":
                    view.showMessage("Logging out...");
                    new HomeCLIController().start(scanner);
                    flag = false;
                    break;
                case "0":
                    System.exit(0);
                    break;
                default:
                    view.showError("Invalid input");
            }
        }
    }
}
