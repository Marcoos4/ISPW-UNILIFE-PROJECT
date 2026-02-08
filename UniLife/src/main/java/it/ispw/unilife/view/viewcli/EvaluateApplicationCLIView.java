package it.ispw.unilife.view.viewcli;

import it.ispw.unilife.bean.ApplicationBean;
import it.ispw.unilife.bean.ApplicationItemBean;
import it.ispw.unilife.bean.DocumentBean;

public class EvaluateApplicationCLIView extends CLIView {
    public void showHeader() {
        System.out.println("\n=================================");
        System.out.println("  UNILIFE - EVALUATE APPLICATION  ");
        System.out.println("=================================");
    }

    public void showApplicationDetails(ApplicationBean app) {
        System.out.println("\nApplicant: " + app.getStudentName().getName() + " " + app.getStudentName().getSurname());
        System.out.println("Course: " + app.getCourseBean().getTitle());
        System.out.println("Submitted: " + app.getSubmissionDate());

        System.out.println("\n--- Application Items ---");
        if (app.getItems() != null) {
            for (int i = 0; i < app.getItems().size(); i++) {
                ApplicationItemBean item = app.getItems().get(i);
                System.out.println("\n" + (i + 1) + ". " + item.getRequirementName() + " [" + item.getType() + "]");
                if ("TEXT".equalsIgnoreCase(item.getType())) {
                    System.out.println("   Content: " + item.getTextContent());
                } else if ("DOCUMENT".equalsIgnoreCase(item.getType())) {
                    DocumentBean doc = item.getDocument();
                    String fileName = (doc != null && doc.getFileName() != null) ? doc.getFileName() : "Attached Document";
                    System.out.println("   File: " + fileName);
                }
            }
        }
    }

    public void showMenu() {
        System.out.println("\nOptions:");
        System.out.println("1. Accept Application");
        System.out.println("2. Reject Application");
        System.out.println("3. Back");
        System.out.println("0. Quit");
        System.out.print("Choose: ");
    }
}
