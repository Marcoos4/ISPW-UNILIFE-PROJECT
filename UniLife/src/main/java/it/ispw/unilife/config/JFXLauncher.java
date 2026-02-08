package it.ispw.unilife.config;

import javafx.application.Application;

public class JFXLauncher extends UILauncher{
    @Override
    public void startUI(){
        Application.launch(UniLIFEApp.class);
    }
}
