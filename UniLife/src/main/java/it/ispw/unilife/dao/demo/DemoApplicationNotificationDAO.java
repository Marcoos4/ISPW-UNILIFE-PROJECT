package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.ApplicationNotificationDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.notification.ApplicationNotification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DemoApplicationNotificationDAO implements ApplicationNotificationDAO {

    private static DemoApplicationNotificationDAO instance = null;
    private final List<ApplicationNotification> cache = new ArrayList<>();

    private DemoApplicationNotificationDAO() {}

    public static DemoApplicationNotificationDAO getInstance() {
        if (instance == null) {
            instance = new DemoApplicationNotificationDAO();
        }
        return instance;
    }

    @Override
    public ApplicationNotification getApplicationNotification(String username, LocalDateTime timestamp) throws DAOException {
        for (ApplicationNotification item : cache) {
            if (item.getSender().getUsername().equals(username) && item.getTimestamp().equals(timestamp)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void insert(ApplicationNotification item) throws DAOException {
        cache.add(item);
    }

    @Override
    public List<ApplicationNotification> getAll() throws DAOException {
        return new ArrayList<>(cache);
    }


    @Override
    public void update(ApplicationNotification item) throws DAOException {
        cache.removeIf(n -> n.getSender().getUsername().equals(item.getSender().getUsername()) && n.getTimestamp().equals(item.getTimestamp()));
        cache.add(item);
    }

    @Override
    public void delete(ApplicationNotification item) throws DAOException {
        cache.removeIf(n -> n.getSender().getUsername().equals(item.getSender().getUsername()) && n.getTimestamp().equals(item.getTimestamp()));
    }
}
