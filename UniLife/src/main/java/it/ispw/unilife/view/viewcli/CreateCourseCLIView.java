package it.ispw.unilife.view.viewcli;

public class CreateCourseCLIView extends CLIView {
    public void showHeader() {
        System.out.println("\n=================================");
        System.out.println("    UNILIFE - CREATE COURSE       ");
        System.out.println("=================================");
    }

    public void promptTitle() { System.out.print("Title: "); }
    public void promptDescription() { System.out.print("Description: "); }
    public void promptLanguage() { System.out.print("Language: "); }
    public void promptDuration() { System.out.print("Duration (months): "); }
    public void promptFees() { System.out.print("Fees (€): "); }
    public void promptTags() { System.out.print("Tags (comma separated, e.g. MATH,SCIENCE): "); }

    public void showCourseTypeMenu() {
        System.out.println("\nCourse Type:");
        System.out.println("1. Undergraduate");
        System.out.println("2. PostGraduate");
        System.out.println("3. Phd");
        System.out.print("Choose: ");
    }

    public void showRequirementsMenu() {
        System.out.println("\nAdmission Requirements:");
        System.out.println("1. Add Text Requirement");
        System.out.println("2. Add Document Requirement");
        System.out.println("3. Done - Confirm & Create");
        System.out.println("4. Cancel");
        System.out.print("Choose: ");
    }

    // Text Requirement prompts
    public void promptReqName() { System.out.print("  Requirement Name: "); }
    public void promptReqDescription() { System.out.print("  Description: "); }
    public void promptReqMinChars() { System.out.print("  Min chars: "); }
    public void promptReqMaxChars() { System.out.print("  Max chars: "); }

    // Document Requirement prompts
    public void promptReqExtension() { System.out.print("  Allowed extension (e.g. pdf): "); }
    public void promptReqMaxSizeMB() { System.out.print("  Max size (MB): "); }
    public void promptReqIsCertificate() { System.out.print("  Is certificate? (y/n): "); }
}
