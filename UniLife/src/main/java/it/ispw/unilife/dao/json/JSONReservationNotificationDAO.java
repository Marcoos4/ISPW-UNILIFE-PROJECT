package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.ReservationNotificationDAO;
import it.ispw.unilife.enums.NotificationStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Reservation;
import it.ispw.unilife.model.User;
import it.ispw.unilife.model.notification.ReservationNotification;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONReservationNotificationDAO implements ReservationNotificationDAO {

    private static final Logger logger = Logger.getLogger(JSONReservationNotificationDAO.class.getName());
    private static final String FILE_NAME = "reservation_notifications.json";
    private static JSONReservationNotificationDAO instance = null;
    private final List<ReservationNotification> cache = new ArrayList<>();

    private JSONReservationNotificationDAO() throws DAOException {
        try {
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static synchronized JSONReservationNotificationDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new JSONReservationNotificationDAO();
        }
        return instance;
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        List<JsonRecords.ReservationNotificationRecord> records = new ArrayList<>();
        for (ReservationNotification n : cache) {
            JsonRecords.ReservationNotificationRecord r = new JsonRecords.ReservationNotificationRecord();
            r.setUsername(n.getSender().getUsername());
            r.setTimestamp(n.getTimestamp());
            r.setStatus(n.getStatus().toString());
            r.setMessage(n.getMessage());
            r.setStudentUsername(n.getReservation().checkStudent().getUsername());
            r.setTutorUsername(n.getReservation().checkTutor().getUsername());
            r.setStart(n.getReservation().checkLesson().getStartTime());
            records.add(r);
        }
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), gson.toJson(records));
    }

    @Override
    public ReservationNotification getReservationNotification(String senderUsername, LocalDateTime date) throws DAOException {
        getAll();
        for (ReservationNotification item : cache) {
            if (item.getSender().getUsername().equals(senderUsername) && item.getTimestamp().equals(date)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public List<ReservationNotification> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        String json = JsonUtil.readFile(file);

        Type listType = new TypeToken<List<JsonRecords.ReservationNotificationRecord>>() {}.getType();
        List<JsonRecords.ReservationNotificationRecord> records = gson.fromJson(json, listType);

        if (records != null) {
            for (JsonRecords.ReservationNotificationRecord r : records) {
                try {
                    User user = JSONUserDAO.getInstance().getUser(r.getUsername());
                    Reservation reservation = JSONReservationDAO.getInstance().getReservation(
                            r.getStudentUsername(),
                            r.getTutorUsername(),
                            r.getStart()
                    );
                    NotificationStatus status = NotificationStatus.fromString(r.getStatus());

                    ReservationNotification notification = new ReservationNotification(r.getMessage(), reservation, user, r.getTimestamp(), status);
                    cache.add(notification);
                } catch (UserNotFoundException e) {
                    logger.log(Level.SEVERE, "User not found", e);
                }
            }
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(ReservationNotification item) throws DAOException {
        cache.add(item);
        saveToFile();
    }

    @Override
    public void update(ReservationNotification item) throws DAOException {
        cache.removeIf(n -> n.getSender().getUsername().equals(item.getSender().getUsername()) && n.getTimestamp().equals(item.getTimestamp()));
        cache.add(item);
        saveToFile();
    }

    @Override
    public void delete(ReservationNotification item) throws DAOException {
        cache.removeIf(n -> n.getSender().getUsername().equals(item.getSender().getUsername()) && n.getTimestamp().equals(item.getTimestamp()));
        saveToFile();
    }
}