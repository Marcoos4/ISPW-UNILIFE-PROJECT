package it.ispw.unilife.view.viewcli;

public class ReviewCLIView extends CLIView {
    public void showHeader() {
        System.out.println("\n=================================");
        System.out.println("    UNILIFE - LEAVE REVIEW        ");
        System.out.println("=================================");
    }

    public void promptRating() {
        System.out.print("Enter rating (1-5): ");
    }

    public void promptReviewText() {
        System.out.print("Enter your review: ");
    }

    public void showMenu() {
        System.out.println("\nOptions:");
        System.out.println("1. Confirm & Send");
        System.out.println("2. Cancel");
        System.out.print("Choose: ");
    }
}
