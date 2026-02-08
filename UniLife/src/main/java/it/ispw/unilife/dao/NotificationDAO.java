package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.User;
import it.ispw.unilife.model.notification.Notification;
import java.util.List;

public interface NotificationDAO extends DAO<Notification>{
    void insert(Notification item, User user) throws DAOException;
    List<Notification> getAll(User user) throws DAOException;
}
