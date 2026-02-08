package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.CourseBean;
import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.controller.CourseDiscoveryAndApplication;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.view.viewcli.CourseDetailCLIView;

import java.util.Scanner;

public class CourseDetailCLIController implements CLIContoller {
    private final CourseDetailCLIView view = new CourseDetailCLIView();
    private final TokenBean tokenBean;
    private CourseBean selectedCourse;
    private final CourseDiscoveryAndApplication courseController = new CourseDiscoveryAndApplication();

    public CourseDetailCLIController(TokenBean tokenBean, CourseBean course) {
        this.tokenBean = tokenBean;
        this.selectedCourse = course;
    }

    @Override
    public void start(Scanner scanner) {
        view.showHeader();

        // Load full course information
        selectedCourse = courseController.findCourseInformation(selectedCourse);

        if (selectedCourse == null) {
            view.showError("No course selected.");
            return;
        }

        view.showCourseDetails(selectedCourse);

        boolean flag = true;
        while (flag) {
            view.showMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    try {
                        new ApplicationFormCLIController(tokenBean, selectedCourse).start(scanner);
                    } catch (DAOException e) {
                        view.showError(e.getMessage());
                    }
                    flag = false;
                    break;
                case "2":
                    System.out.println("Not Implemented");
                    break;
                case "3":
                    view.showMessage("Added to favourites: " + selectedCourse.getTitle());
                    break;
                case "4":
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
