package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.controller.CourseDiscoveryAndApplication;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.InvalidCertificateException;
import it.ispw.unilife.view.viewcli.ApplicationFormCLIView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ApplicationFormCLIController implements CLIContoller {
    private final ApplicationFormCLIView view = new ApplicationFormCLIView();
    private final TokenBean tokenBean;
    private final CourseBean selectedCourse;
    private final CourseDiscoveryAndApplication appController = new CourseDiscoveryAndApplication();

    private final Map<String, String> textInputMap = new HashMap<>();
    private final Map<String, File> fileInputMap = new HashMap<>();
    private List<RequirementBean> requirements;

    public ApplicationFormCLIController(TokenBean tokenBean, CourseBean course) {
        this.tokenBean = tokenBean;
        this.selectedCourse = course;
    }

    @Override
    public void start(Scanner scanner) throws DAOException {
        view.showHeader(selectedCourse.getTitle());

        // Load requirements
        AdmissionRequirementBean reqBean = appController.findCourseAdmissionRequirements(selectedCourse);

        if (reqBean == null || reqBean.getRequirements() == null || reqBean.getRequirements().isEmpty()) {
            view.showNoRequirements();
            requirements = new ArrayList<>();
        } else {
            requirements = reqBean.getRequirements();
            collectInputs(scanner);
        }

        boolean flag = true;
        while (flag) {
            view.showMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    onSubmit();
                    flag = false;
                    break;
                case "2":
                    view.showMessage("Draft saved.");
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

    private void collectInputs(Scanner scanner) {
        for (RequirementBean req : requirements) {
            processRequirement(scanner, req);
        }
    }

    private void processRequirement(Scanner scanner, RequirementBean req) {
        if ("TEXT".equalsIgnoreCase(req.getType())) {
            handleTextRequirement(scanner, req);
        } else if ("DOCUMENT".equalsIgnoreCase(req.getType())) {
            handleDocRequirement(scanner, req);
        }
    }

    private void handleTextRequirement(Scanner scanner, RequirementBean req) {
        view.showTextRequirement(req);
        String text = scanner.nextLine();
        textInputMap.put(req.getName(), text);
    }

    private void handleDocRequirement(Scanner scanner, RequirementBean req) {
        view.showDocRequirement(req);
        String filePath = scanner.nextLine().trim();

        if (filePath.isEmpty()) {
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            view.showError("File not found: " + filePath);
            return;
        }

        if (isFileSizeTooLarge(file, req)) {
            view.showError("File too large! Max " + req.getMaxSizeMB() + "MB");
            return;
        }

        fileInputMap.put(req.getName(), file);
        view.showMessage("File loaded: " + file.getName());
    }

    private boolean isFileSizeTooLarge(File file, RequirementBean req) {
        double megabytes = file.length() / (1024.0 * 1024.0);
        return megabytes > req.getMaxSizeMB();
    }

    private void onSubmit() {
        try {
            ApplicationBean appBean = new ApplicationBean();
            appBean.setCourseBean(selectedCourse);

            List<ApplicationItemBean> items = new ArrayList<>();

            // Text items
            for (Map.Entry<String, String> entry : textInputMap.entrySet()) {
                ApplicationItemBean item = new ApplicationItemBean();
                item.setRequirementName(entry.getKey());
                item.setType("TEXT");
                item.setTextContent(entry.getValue());
                items.add(item);
            }

            // Document items
            for (Map.Entry<String, File> entry : fileInputMap.entrySet()) {
                ApplicationItemBean item = new ApplicationItemBean();
                item.setRequirementName(entry.getKey());
                item.setType("DOCUMENT");

                    DocumentBean docBean = fileToDocumentBean(entry.getValue());
                    item.setDocument(docBean);
                    items.add(item);
            }

            appBean.setItems(items);

            if (tokenBean == null) {
                view.showError("You are not logged in.");
                return;
            }

            appController.submitApplication(tokenBean, appBean);
            view.showMessage("Application submitted successfully!");
        }  catch (IOException e) {
            view.showError("Error reading file: ");
        } catch (InvalidCertificateException e) {
            view.showError("Your Certification is not valid: " + e.getMessage());
        } catch (Exception e) {
            view.showError("Submission Failed: " + e.getMessage());
        }
    }

    private DocumentBean fileToDocumentBean(File file) throws IOException {
        DocumentBean docBean = new DocumentBean();
        docBean.setFileName(file.getName());

        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf > 0 && lastIndexOf < name.length() - 1) {
            docBean.setFileType(name.substring(lastIndexOf + 1).toUpperCase());
        } else {
            docBean.setFileType("");
        }

        docBean.setFileSize(file.length() / 1024.0);
        docBean.setContent(Files.readAllBytes(file.toPath()));
        return docBean;
    }
}
