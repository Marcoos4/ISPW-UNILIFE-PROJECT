package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.ApplicationBean;
import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.controller.CourseDiscoveryAndApplication;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.view.viewcli.EvaluateApplicationCLIView;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvaluateApplicationCLIController implements CLIContoller {
    private static final Logger logger = Logger.getLogger(EvaluateApplicationCLIController.class.getName());

    private final EvaluateApplicationCLIView view = new EvaluateApplicationCLIView();
    private final TokenBean tokenBean;
    private final ApplicationBean currentApplication;
    private final CourseDiscoveryAndApplication manageApplicationController = new CourseDiscoveryAndApplication();

    public EvaluateApplicationCLIController(TokenBean tokenBean, ApplicationBean application) {
        this.tokenBean = tokenBean;
        this.currentApplication = application;
    }

    @Override
    public void start(Scanner scanner) {
        view.showHeader();

        if (currentApplication == null) {
            view.showError("No application data.");
            return;
        }

        view.showApplicationDetails(currentApplication);

        boolean flag = true;
        while (flag) {
            view.showMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    currentApplication.setStatus("ACCEPTED");
                    submitEvaluation();
                    flag = false;
                    break;
                case "2":
                    currentApplication.setStatus("REJECTED");
                    submitEvaluation();
                    flag = false;
                    break;
                case "3":
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

    private void submitEvaluation() {
        try {
            manageApplicationController.evaluateApplication(tokenBean, currentApplication);
            view.showMessage("Registered: " + currentApplication.getStatus());
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error during evaluation", e);
            view.showError("Unable to complete the operation.");
        } catch (UserNotFoundException e) {
            System.out.println("User not found: " + e.getMessage());
        }
    }
}
