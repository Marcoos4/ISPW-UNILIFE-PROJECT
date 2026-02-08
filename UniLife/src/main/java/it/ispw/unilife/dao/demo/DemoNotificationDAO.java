package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.NotificationDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.User;
import it.ispw.unilife.model.notification.ApplicationNotification;
import it.ispw.unilife.model.notification.LessonNotification;
import it.ispw.unilife.model.notification.Notification;
import it.ispw.unilife.model.notification.ReservationNotification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoNotificationDAO implements NotificationDAO {

    private static DemoNotificationDAO instance = null;
    // Mappa username -> lista notifiche
    private final Map<String, List<Notification>> cache = new HashMap<>();

    private DemoNotificationDAO() {}

    public static DemoNotificationDAO getInstance() {
        if (instance == null) {
            instance = new DemoNotificationDAO();
        }
        return instance;
    }

    @Override
    public List<Notification> getAll(User user) throws DAOException {
        return new ArrayList<>(cache.getOrDefault(user.getUsername(), new ArrayList<>()));
    }

    @Override
    public List<Notification> getAll() throws DAOException {
        throw new UnsupportedOperationException("Not supported. Use getAll(User user).");
    }

    @Override
    public void insert(Notification item, User user) throws DAOException {
        cache.computeIfAbsent(user.getUsername(), k -> new ArrayList<>()).add(item);

        if (item instanceof ReservationNotification) {
            DemoReservationNotificationDAO.getInstance().insert((ReservationNotification) item);
        } else if (item instanceof LessonNotification) {
            DemoLessonNotificationDAO.getInstance().insert((LessonNotification) item);
        } else if (item instanceof ApplicationNotification) {
            DemoApplicationNotificationDAO.getInstance().insert((ApplicationNotification) item);
        }
    }

    @Override
    public void insert(Notification notification) throws DAOException {
        throw new UnsupportedOperationException("Use insert(Notification item, User recipient) instead.");
    }

    @Override
    public void update(Notification item) throws DAOException {
        if (item instanceof ReservationNotification) {
            DemoReservationNotificationDAO.getInstance().update((ReservationNotification) item);
        } else if (item instanceof LessonNotification) {
            DemoLessonNotificationDAO.getInstance().update((LessonNotification) item);
        } else if (item instanceof ApplicationNotification) {
            DemoApplicationNotificationDAO.getInstance().update((ApplicationNotification) item);
        }
    }

    @Override
    public void delete(Notification item) throws DAOException {
        throw new UnsupportedOperationException("Use delete(Notification item, User recipient) instead.");
    }
}
