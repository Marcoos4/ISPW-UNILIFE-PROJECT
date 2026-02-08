package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.ReservationNotificationDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.notification.ReservationNotification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DemoReservationNotificationDAO implements ReservationNotificationDAO {

    private static DemoReservationNotificationDAO instance = null;
    private final List<ReservationNotification> cache = new ArrayList<>();

    private DemoReservationNotificationDAO() {}

    public static DemoReservationNotificationDAO getInstance() {
        if (instance == null) {
            instance = new DemoReservationNotificationDAO();
        }
        return instance;
    }

    @Override
    public ReservationNotification getReservationNotification(String senderUsername, LocalDateTime date) throws DAOException {
        for (ReservationNotification item : cache) {
            if (item.getSender().getUsername().equals(senderUsername) && item.getTimestamp().equals(date)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public List<ReservationNotification> getAll() throws DAOException {
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(ReservationNotification item) throws DAOException {
        cache.add(item);
    }

    @Override
    public void update(ReservationNotification item) throws DAOException {
        cache.removeIf(n -> n.getSender().getUsername().equals(item.getSender().getUsername()) && n.getTimestamp().equals(item.getTimestamp()));
        cache.add(item);
    }

    @Override
    public void delete(ReservationNotification item) throws DAOException {
        cache.removeIf(n -> n.getSender().getUsername().equals(item.getSender().getUsername()) && n.getTimestamp().equals(item.getTimestamp()));
    }
}
