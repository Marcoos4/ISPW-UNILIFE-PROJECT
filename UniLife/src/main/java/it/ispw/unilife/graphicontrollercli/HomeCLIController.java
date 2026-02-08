package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.view.viewcli.HomeCLIView;

import java.util.Scanner;

public class HomeCLIController implements CLIContoller {
    private final HomeCLIView view;

    public HomeCLIController() {
        this.view = new HomeCLIView();
    }

    @Override
    public void start(Scanner scanner) {
        boolean flag = true;

        while (flag) {
            view.showGuestMenu();
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    new LoginCLIController().start(scanner);
                    break;

                case "2":
                    new RegisterCLIController().start(scanner);
                    break;

                case "0":
                    flag = false;
                    break;
                default:
                    view.showError("Invalid input");
            }
        }
    }
}
