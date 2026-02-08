package it.ispw.unilife.view.viewcli;

import it.ispw.unilife.bean.CourseBean;

public class CourseDetailCLIView extends CLIView {
    public void showHeader() {
        System.out.println("\n=================================");
        System.out.println("    UNILIFE - COURSE DETAIL       ");
        System.out.println("=================================");
    }

    public void showCourseDetails(CourseBean course) {
        System.out.println("\n--- " + course.getTitle() + " ---");
        System.out.println("Description: " + course.getDescription());
        System.out.println("Duration: " + course.getDuration() + " months");
        System.out.println("Fees: € " + String.format("%.2f", course.getFees()));
        System.out.println("Language: " + course.getLanguageOfInstruction());
        System.out.println("Course Type: " + course.getCourseType());

        if (course.getUniversity() != null) {
            System.out.println("\n--- University Info ---");
            System.out.println("Name: " + course.getUniversity().getName());
            System.out.println("Location: " + course.getUniversity().getLocation());
            System.out.println("Living Cost: € " + String.format("%.2f", course.getUniversity().getLivingCosts()));
            System.out.println("Ranking: #" + course.getUniversity().getRanking());
        }
    }

    public void showMenu() {
        System.out.println("\nOptions:");
        System.out.println("1. Apply to Course");
        System.out.println("2. View Course Reviews");
        System.out.println("3. Add to Favourites");
        System.out.println("4. Back to Search");
        System.out.println("0. Quit");
        System.out.print("Choose: ");
    }
}
