package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.LessonNotificationDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.notification.LessonNotification;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DemoLessonNotificationDAO implements LessonNotificationDAO {

    private static DemoLessonNotificationDAO instance = null;
    private final List<LessonNotification> cache = new ArrayList<>();

    private DemoLessonNotificationDAO() {}

    public static DemoLessonNotificationDAO getInstance() {
        if (instance == null) {
            instance = new DemoLessonNotificationDAO();
        }
        return instance;
    }

    @Override
    public LessonNotification getLessonNotification(String username, LocalDateTime timestamp) throws DAOException {
        for (LessonNotification item : cache) {
            if (item.getSender().getUsername().equals(username) &&
                    item.getTimestamp().truncatedTo(ChronoUnit.SECONDS)
                            .equals(timestamp.truncatedTo(ChronoUnit.SECONDS))) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void insert(LessonNotification item) throws DAOException {
        cache.add(item);
    }

    @Override
    public List<LessonNotification> getAll() throws DAOException {
        return new ArrayList<>(cache);
    }

    @Override
    public void update(LessonNotification item) throws DAOException {
        cache.removeIf(n -> n.getSender().getUsername().equals(item.getSender().getUsername()) && n.getTimestamp().equals(item.getTimestamp()));
        cache.add(item);
    }

    @Override
    public void delete(LessonNotification item) throws DAOException {
        cache.removeIf(n -> n.getSender().getUsername().equals(item.getSender().getUsername()) && n.getTimestamp().equals(item.getTimestamp()));
    }
}
