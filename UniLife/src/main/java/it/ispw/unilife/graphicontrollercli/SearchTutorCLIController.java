package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.controller.BookTutor;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.InvalidTokenException;
import it.ispw.unilife.view.viewcli.SearchTutorCLIView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchTutorCLIController implements CLIContoller {
    private static final Logger LOGGER = Logger.getLogger(SearchTutorCLIController.class.getName());

    private final SearchTutorCLIView view = new SearchTutorCLIView();
    private final TokenBean tokenBean;
    private final BookTutor bookTutorController = new BookTutor();
    private List<LessonBean> currentLessons;
    private List<String> availableSubjects;

    public SearchTutorCLIController(TokenBean tokenBean) {
        this.tokenBean = tokenBean;
    }

    @Override
    public void start(Scanner scanner) {
        view.showHeader();

        availableSubjects = bookTutorController.getAvailableSubjects();
        loadLessons(new FilterTutorBean());

        boolean flag = true;
        while (flag) {
            view.showMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    onApplyFilters(scanner);
                    break;
                case "2":
                    onSelectLesson(scanner);
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

    private void loadLessons(FilterTutorBean filter) {
        try {
            currentLessons = bookTutorController.filterTutor(filter);
            view.showLessonList(currentLessons);
        } catch (DAOException e) {
            LOGGER.log(Level.WARNING, "Error loading lessons", e);
            view.showError("Database connection error.");
        }
    }

    private void onApplyFilters(Scanner scanner) {
        FilterTutorBean filter = new FilterTutorBean();

        // Subject
        if (availableSubjects != null && !availableSubjects.isEmpty()) {
            view.showSubjectMenu(availableSubjects);
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= availableSubjects.size()) {
                    filter.setSubject(availableSubjects.get(choice - 1));
                }
            } catch (NumberFormatException e) {
                // skip
            }
        }

        // Date
        view.promptDate();
        String dateStr = scanner.nextLine().trim();
        if (!dateStr.isEmpty()) {
            try {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate selectedDate = LocalDate.parse(dateStr, df);
                filter.setStart(selectedDate.atStartOfDay());
                filter.setEnd(selectedDate.atTime(LocalTime.MAX));
            } catch (DateTimeParseException e) {
                view.showError("Invalid date format. Skipping date filter.");
            }
        }
        filter.setAmount(0);

        loadLessons(filter);
    }

    private void onSelectLesson(Scanner scanner) {
        if (currentLessons == null || currentLessons.isEmpty()) {
            view.showError("No lessons to select.");
            return;
        }

        view.promptLessonNumber();
        try {
            int num = Integer.parseInt(scanner.nextLine().trim());
            if (num < 1 || num > currentLessons.size()) {
                view.showError("Invalid lesson number.");
                return;
            }

        LessonBean selectedLesson = currentLessons.get(num - 1);

            ReservationBean reservationBean = bookTutorController.startReservationProcedure(tokenBean, selectedLesson);
            new TutorBookingCLIController(tokenBean, reservationBean).start(scanner);

        } catch (InvalidTokenException e) {
            LOGGER.severe("Session expired");
            view.showError("Session expired. Please log in again.");
        } catch (NumberFormatException e) {
            view.showError("Invalid input.");
        }
    }
}
