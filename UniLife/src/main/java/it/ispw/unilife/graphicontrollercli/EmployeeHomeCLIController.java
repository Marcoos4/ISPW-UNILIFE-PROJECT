package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.view.viewcli.EmployeeHomeCLIView;

import java.util.Scanner;

public class EmployeeHomeCLIController implements CLIContoller {
    private final EmployeeHomeCLIView view = new EmployeeHomeCLIView();
    private final TokenBean tokenBean;

    public EmployeeHomeCLIController(TokenBean tokenBean) {
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
                    new CreateCourseCLIController(tokenBean).start(scanner);
                    break;
                case "2":
                    new ProfileCLIController(tokenBean).start(scanner);
                    break;
                case "3":
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
