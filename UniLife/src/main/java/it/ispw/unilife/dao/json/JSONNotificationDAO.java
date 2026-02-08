package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.NotificationDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.User;
import it.ispw.unilife.model.notification.ApplicationNotification;
import it.ispw.unilife.model.notification.LessonNotification;
import it.ispw.unilife.model.notification.Notification;
import it.ispw.unilife.model.notification.ReservationNotification;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONNotificationDAO implements NotificationDAO {

    private static final Logger logger = Logger.getLogger(JSONNotificationDAO.class.getName());
    private static final String FILE_NAME = "user_notifications.json";
    private static JSONNotificationDAO instance = null;
    private final List<JsonRecords.UserNotificationRecord> records = new ArrayList<>();

    private JSONNotificationDAO() {
        loadFromFile();
    }

    public static synchronized JSONNotificationDAO getInstance() {
        if (instance == null) {
            instance = new JSONNotificationDAO();
        }
        return instance;
    }

    private void loadFromFile() {
        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return;

        String json = JsonUtil.readFile(file);
        Type listType = new TypeToken<List<JsonRecords.UserNotificationRecord>>() {}.getType();
        List<JsonRecords.UserNotificationRecord> loaded = gson.fromJson(json, listType);
        if (loaded != null) {
            records.addAll(loaded);
        }
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        String json = gson.toJson(records);
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), json);
    }

    @Override
    public List<Notification> getAll(User user) throws DAOException {
        List<Notification> notifications = new ArrayList<>();

        for (JsonRecords.UserNotificationRecord r : records) {
            if (r.getUsername().equals(user.getUsername())) {
                Notification notification = JSONReservationNotificationDAO.getInstance()
                        .getReservationNotification(r.getSenderUsername(), r.getTimestamp());

                if (notification == null) {
                    notification = JSONApplicationNotificationDAO.getInstance()
                            .getApplicationNotification(r.getSenderUsername(), r.getTimestamp());
                }

                if (notification == null) {
                    notification = JSONLessonNotificationDAO.getInstance()
                            .getLessonNotification(r.getSenderUsername(), r.getTimestamp());
                }

                if (notification != null) {
                    notifications.add(notification);
                }
            }
        }
        return notifications;
    }

    @Override
    public List<Notification> getAll() throws DAOException {
        throw new UnsupportedOperationException("Not supported. Use getAll(User user).");
    }

    @Override
    public void insert(Notification item, User user) throws DAOException {
        JsonRecords.UserNotificationRecord r = new JsonRecords.UserNotificationRecord();
        r.setUsername(user.getUsername());
        r.setSenderUsername(item.getSender().getUsername());
        r.setTimestamp(item.getTimestamp());
        records.add(r);
        saveToFile();

        if (item instanceof ReservationNotification reservationNotification) {
            JSONReservationNotificationDAO.getInstance().insert(reservationNotification);
        } else if (item instanceof LessonNotification lessonNotification) {
            JSONLessonNotificationDAO.getInstance().insert(lessonNotification);
        } else if (item instanceof ApplicationNotification applicationNotification) {
            JSONApplicationNotificationDAO.getInstance().insert(applicationNotification);
        }

        logger.info("Notifica salvata correttamente per: " + user.getUsername());
    }

    @Override
    public void insert(Notification notification) throws DAOException {
        throw new UnsupportedOperationException("Use insert(Notification item, User recipient) instead.");
    }

    @Override
    public void update(Notification item) throws DAOException {
        if (item instanceof ReservationNotification reservationNotification) {
            JSONReservationNotificationDAO.getInstance().update(reservationNotification);
        } else if (item instanceof LessonNotification lessonNotification) {
            JSONLessonNotificationDAO.getInstance().update(lessonNotification);
        } else if (item instanceof ApplicationNotification applicationNotification) {
            JSONApplicationNotificationDAO.getInstance().update(applicationNotification);
        }
    }

    @Override
    public void delete(Notification item) throws DAOException {
        Logger.getLogger(JSONNotificationDAO.class.getName()).log(Level.INFO, "Delete notifica correttamente");
    }
}