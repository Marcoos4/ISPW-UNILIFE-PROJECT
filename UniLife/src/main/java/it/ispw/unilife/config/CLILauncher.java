package it.ispw.unilife.config;

import it.ispw.unilife.graphicontrollercli.HomeCLIController;

import java.util.Scanner;
import java.util.logging.Logger;

public class CLILauncher extends UILauncher{

    private static final Logger logger = Logger.getLogger(CLILauncher.class.getName());

    @Override
    public void startUI(){

        logger.info("System Booting...");
        HomeCLIController homeController = new HomeCLIController();
        Scanner scanner = new Scanner(System.in);
        homeController.start(scanner);
        logger.info("System Shutdown.");
    }
}
