package it.ispw.unilife.view.viewcli;

import it.ispw.unilife.bean.CourseBean;

import java.util.List;
import java.util.Set;

public class SearchCourseCLIView extends CLIView {
    public void showHeader() {
        System.out.println("\n=================================");
        System.out.println("    UNILIFE - SEARCH COURSES      ");
        System.out.println("=================================");
    }

    public void showMenu() {
        System.out.println("\nOptions:");
        System.out.println("1. Search by Name");
        System.out.println("2. Apply Filters");
        System.out.println("3. Select a Course");
        System.out.println("4. Back to Home");
        System.out.println("0. Quit");
        System.out.print("Choose: ");
    }

    public void promptSearchText() { System.out.print("Enter search term: "); }

    public void showFilterMenu() {
        System.out.println("\n--- Filters ---");
    }

    public void promptFilterOption(String label, Set<String> options) {
        if (options == null || options.isEmpty()) return;
        System.out.println(label + ":");
        int i = 1;
        for (String opt : options) {
            System.out.println("  " + i + ". " + opt);
            i++;
        }
        System.out.print("Choose (0 to skip): ");
    }

    public void showCourseList(List<CourseBean> courses) {
        System.out.println("\n--- Courses Found ---");
        if (courses == null || courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }
        for (int i = 0; i < courses.size(); i++) {
            CourseBean c = courses.get(i);
            String uniName = (c.getUniversity() != null && c.getUniversity().getName() != null) ? c.getUniversity().getName() : "N/A";
            System.out.println((i + 1) + ". " + c.getTitle() + " - " + uniName);
            if (c.getTags() != null && !c.getTags().isEmpty()) {
                System.out.println("   Tags: " + String.join(", ", c.getTags()));
            }
        }
    }

    public void promptCourseNumber() { System.out.print("Enter course number: "); }
}
