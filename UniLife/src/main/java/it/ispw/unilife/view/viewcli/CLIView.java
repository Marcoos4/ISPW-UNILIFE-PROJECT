package it.ispw.unilife.view.viewcli;

public class CLIView {
    public void showMessage(String msg) {
        System.out.println(">> " + msg);
    }

    public void showError(String msg) {
        System.out.println("[ERROR] " + msg);
    }

    public void showSeparator() {
        System.out.println("=================================");
    }
}
