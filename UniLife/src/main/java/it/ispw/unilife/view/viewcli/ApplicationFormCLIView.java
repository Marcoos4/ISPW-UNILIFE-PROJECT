package it.ispw.unilife.view.viewcli;

import it.ispw.unilife.bean.RequirementBean;

public class ApplicationFormCLIView extends CLIView {
    public void showHeader(String courseTitle) {
        System.out.println("\n=================================");
        System.out.println("    UNILIFE - APPLICATION FORM    ");
        System.out.println("=================================");
        System.out.println("Course: " + courseTitle);
    }

    public void showTextRequirement(RequirementBean req) {
        System.out.println("\n--- " + req.getLabel() + " ---");
        System.out.println("Description: " + req.getDescription());
        System.out.println("(Min chars: " + req.getMinChars() + " - Max chars: " + req.getMaxChars() + ")");
        System.out.print("Enter text: ");
    }

    public void showDocRequirement(RequirementBean req) {
        String extensions = (req.getAllowedExtension() != null) ? req.getAllowedExtension() : "*.*";
        System.out.println("\n--- " + req.getLabel() + " (Document) ---");
        System.out.println("Max " + req.getMaxSizeMB() + "MB (" + extensions + ")");
        System.out.print("Enter file path: ");
    }

    public void showMenu() {
        System.out.println("\nOptions:");
        System.out.println("1. Submit Application");
        System.out.println("2. Save Draft");
        System.out.println("3. Back to Course Detail");
        System.out.println("0. Quit");
        System.out.print("Choose: ");
    }

    public void showNoRequirements() {
        System.out.println("No specific requirements found for this course.");
    }
}
