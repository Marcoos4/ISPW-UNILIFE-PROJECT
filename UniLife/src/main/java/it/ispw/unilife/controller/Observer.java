package it.ispw.unilife.controller;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.notification.Subject;


public interface Observer {
    void update(Subject subject, Object arg, String token) throws DAOException;
}