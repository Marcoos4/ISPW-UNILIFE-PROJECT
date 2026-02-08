package it.ispw.unilife.config;

import java.util.logging.Logger;

public abstract class UILauncher {

    private static final Logger LOGGER = Logger.getLogger(UILauncher.class.getName());

    public static UILauncher getLauncher(){
        Configuration config = Configuration.getInstance();
        UiMode uiMode = config.getUiMode();
        String uiString = String.valueOf(uiMode);
        LOGGER.info(uiString);
        if (uiMode.equals(UiMode.JFX))
            return new JFXLauncher();
        else return new CLILauncher();
    }

    public abstract void startUI();
}
