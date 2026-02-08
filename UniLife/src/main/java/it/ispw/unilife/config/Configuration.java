package it.ispw.unilife.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {

    private static final Logger logger = Logger.getLogger(Configuration.class.getName());
    private static Configuration instance;

    private static final String CONFIG_UI_MODE_PROP = "ui_mode";
    private static final String CONFIG_PERSISTENCY_MODE_PROP = "persistency_mode";

    private static final String CONFIG_FILE_PATH = "/config.properties";

    private UiMode uiMode = UiMode.JFX;
    private PersistencyMode persistencyMode = PersistencyMode.JDBC;
    private boolean loaded = false;

    private Configuration() {}

    public static synchronized Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public void loadConfiguration(String[] args) throws IllegalStateException {

        if (loaded)
            throw new IllegalStateException("Config already loaded!");

        try (InputStream confIn = Configuration.class.getResourceAsStream(CONFIG_FILE_PATH)) {

            if (confIn != null) {
                Properties props = new Properties();
                props.load(confIn);

                String configUiMode = props.getProperty(CONFIG_UI_MODE_PROP);
                if (configUiMode != null) {
                    this.uiMode = UiMode.valueOf(configUiMode.toUpperCase());
                }

                String configPersMode = props.getProperty(CONFIG_PERSISTENCY_MODE_PROP);
                if (configPersMode != null) {
                    this.persistencyMode = PersistencyMode.valueOf(configPersMode.toUpperCase());
                }

                logger.info("Loaded config from " + CONFIG_FILE_PATH);
            } else {
                logger.info("Couldn't find " + CONFIG_FILE_PATH + " inside resources. Using Default config!");
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE,"Error reading " + CONFIG_FILE_PATH + ". Using Default config!",e);
        } catch (IllegalArgumentException e) {
            logger.info("Invalid values passed inside config files. Using Default config!");
        }

        for (String arg : args) {
            switch (arg.toLowerCase()) {
                case "--cli":
                    this.uiMode = UiMode.CLI;
                    break;
                case "--gui", "--jfx":
                    this.uiMode = UiMode.JFX;
                    break;
                case "--json", "--fs":
                    this.persistencyMode = PersistencyMode.JSON;
                    break;
                case "--jdbc", "--db":
                    this.persistencyMode = PersistencyMode.JDBC;
                    break;
                case "--demo":
                    this.persistencyMode = PersistencyMode.DEMO;
                    break;
                default:
                    break;
            }
        }

        loaded = true;
    }

    public PersistencyMode getPersistencyMode() {
        return persistencyMode;
    }
    public UiMode getUiMode() {
        return uiMode;
    }


}