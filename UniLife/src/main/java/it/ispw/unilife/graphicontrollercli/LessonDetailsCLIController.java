package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.LessonBean;
import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.controller.AddTutor;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.EmployeeNotFoundException;
import it.ispw.unilife.view.viewcli.LessonDetailsCLIView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LessonDetailsCLIController implements CLIContoller {
    private static final Logger LOGGER = Logger.getLogger(LessonDetailsCLIController.class.getName());

    private final LessonDetailsCLIView view = new LessonDetailsCLIView();
    private final TokenBean tokenBean;
    private final AddTutor addTutorController = new AddTutor();

    public LessonDetailsCLIController(TokenBean tokenBean) {
        this.tokenBean = tokenBean;
    }

    @Override
    public void start(Scanner scanner) {
        view.showHeader();

        // 1. Input Collection
        String subject = promptForInput(scanner, view::promptSubject);
        if (subject.isEmpty()) {
            view.showError("Subject is required");
            return;
        }

        String durationStr = promptForInput(scanner, view::showDurationMenu);
        String dateStr = promptForInput(scanner, view::promptDate);
        String timeStr = promptForInput(scanner, view::showStartTimeMenu);
        String priceStr = promptForInput(scanner, view::promptPrice);

        // 2. Validation
        if (!validateInputs(durationStr, dateStr, timeStr, priceStr)) {
            return;
        }

        // 3. Confirmation Flow
        view.showMenu();
        String input = scanner.nextLine().trim();

        if (!"1".equals(input)) {
            view.showMessage("Cancelled.");
            return;
        }

        // 4. Execution (Isolated from UI logic)
        processLessonCreation(subject, durationStr, dateStr, timeStr, priceStr);
    }

// --- Helper Methods ---

    private void processLessonCreation(String subject, String durationStr, String dateStr, String timeStr, String priceStr) {
        try {
            LessonBean lessonBean = buildLessonBean(subject, durationStr, dateStr, timeStr, priceStr);

            if (tokenBean != null) {
                addTutorController.startTutorLessonProcedure(tokenBean, lessonBean);
            }
            view.showMessage("Lesson added successfully!");

        } catch (NumberFormatException e) {
            view.showError("Invalid number format.");
        } catch (DateTimeParseException e) {
            view.showError("Invalid date format. Use dd/MM/yyyy.");
        } catch (DAOException e) {
            LOGGER.log(Level.WARNING, "Error adding lesson", e);
            view.showError("Error adding lesson. Please try again.");
        } catch (EmployeeNotFoundException e) {
            view.showError("Employee not found: " + e.getMessage());
        }
    }

    private LessonBean buildLessonBean(String subject, String durationStr, String dateStr, String timeStr, String priceStr) {
        int duration = Integer.parseInt(durationStr);
        float price = Float.parseFloat(priceStr);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(dateStr, df);

        // Assumes timeStr is in "HH:mm" format (based on original split logic)
        String[] timeParts = timeStr.split(":");
        LocalTime time = LocalTime.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
        LocalDateTime startTime = LocalDateTime.of(date, time);

        LessonBean lessonBean = new LessonBean();
        lessonBean.setSubject(subject);
        lessonBean.setDurationInHours(duration);
        lessonBean.setStartTime(startTime);
        lessonBean.setPrice(price);
        lessonBean.setEndTime(startTime.plusHours(duration));

        return lessonBean;
    }

    private boolean validateInputs(String duration, String date, String time, String price) {
        if (duration.isEmpty()) { view.showError("Duration is required"); return false; }
        if (date.isEmpty())     { view.showError("Date is required"); return false; }
        if (time.isEmpty())     { view.showError("Start time is required"); return false; }
        if (price.isEmpty())    { view.showError("Price is required"); return false; }
        return true;
    }

    // Helper interface to allow passing void view methods
    private String promptForInput(Scanner scanner, Runnable viewPrompt) {
        viewPrompt.run();
        return scanner.nextLine().trim();
    }
}
