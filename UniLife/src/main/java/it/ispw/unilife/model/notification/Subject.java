package it.ispw.unilife.model.notification;

import it.ispw.unilife.controller.Observer;
import it.ispw.unilife.exception.DAOException;

import java.util.ArrayList;
import java.util.List;


public abstract class Subject {
    private final List<Observer> observers = new ArrayList<>();

    public void attach(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void detach(Observer observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(Object arg, String token) throws DAOException {
        for (Observer observer : observers) {
            observer.update(this, arg, token);
        }
    }

}