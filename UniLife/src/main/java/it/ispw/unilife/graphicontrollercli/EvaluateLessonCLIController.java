package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.LessonBean;
import it.ispw.unilife.bean.NotificationBean;
import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.controller.AddTutor;
import it.ispw.unilife.controller.NotificationSystem;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.view.viewcli.EvaluateLessonCLIView;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvaluateLessonCLIController implements CLIContoller {
    private static final Logger logger = Logger.getLogger(EvaluateLessonCLIController.class.getName());

    private final EvaluateLessonCLIView view = new EvaluateLessonCLIView();
    private final TokenBean tokenBean;
    private final NotificationBean currentNotification;
    private LessonBean currentLesson;
    private final AddTutor addTutorController = new AddTutor();
    private final NotificationSystem notificationSystem = NotificationSystem.getInstance();

    public EvaluateLessonCLIController(TokenBean tokenBean, NotificationBean notification) {
        this.tokenBean = tokenBean;
        this.currentNotification = notification;
    }

    @Override
    public void start(Scanner scanner) {
        view.showHeader();

        try {
            currentLesson = notificationSystem.getLessonFromNotification(tokenBean, currentNotification);

            if (currentLesson == null) {
                view.showError("Unable to retrieve lesson details.");
                return;
            }

            view.showLessonDetails(currentLesson);

        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error retrieving lesson", e);
            view.showError("Database communication error.");
            return;
        }

        boolean flag = true;
        while (flag) {
            view.showMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    onAccept();
                    flag = false;
                    break;
                case "2":
                    onReject();
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

    private void onAccept() {
        if (currentLesson == null) return;
        try {
            addTutorController.acceptLesson(tokenBean, currentLesson);
            view.showMessage("Lesson approved successfully.");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error accepting lesson", e);
            view.showError("Unable to complete the operation.");
        }
    }

    private void onReject() {
        if (currentLesson == null) return;
        try {
            addTutorController.rejectLesson(tokenBean, currentLesson);
            view.showMessage("Lesson rejected successfully.");
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error rejecting lesson", e);
            view.showError("Unable to complete the operation.");
        }
    }
}
