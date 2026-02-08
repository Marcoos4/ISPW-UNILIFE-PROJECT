package it.ispw.unilife;

import it.ispw.unilife.config.Configuration;
import it.ispw.unilife.config.UILauncher;

public class Main {
    public static void main(String[] args) {
        try {
            Configuration.getInstance().loadConfiguration(args);
        } catch (IllegalStateException e) {
            System.err.println("Tried to reload App config: " + e.getMessage());
            return;
        }

        UILauncher launcher = UILauncher.getLauncher();
        launcher.startUI();
    }
}
