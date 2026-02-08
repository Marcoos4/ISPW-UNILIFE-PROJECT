package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.ApplicationNotificationDAO;
import it.ispw.unilife.enums.NotificationStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.User;
import it.ispw.unilife.model.admission.Application;
import it.ispw.unilife.model.notification.ApplicationNotification;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONApplicationNotificationDAO implements ApplicationNotificationDAO {

    private static final Logger logger = Logger.getLogger(JSONApplicationNotificationDAO.class.getName());
    private static final String FILE_NAME = "application_notifications.json";
    private static JSONApplicationNotificationDAO instance = null;
    private final List<ApplicationNotification> cache = new ArrayList<>();

    private JSONApplicationNotificationDAO() throws DAOException {
        try {
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static synchronized JSONApplicationNotificationDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new JSONApplicationNotificationDAO();
        }
        return instance;
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        List<JsonRecords.ApplicationNotificationRecord> records = new ArrayList<>();
        for (ApplicationNotification n : cache) {
            JsonRecords.ApplicationNotificationRecord r = new JsonRecords.ApplicationNotificationRecord();
            r.setUsername(n.getSender().getUsername());
            r.setTimestamp(n.getTimestamp());
            r.setStatus(n.getStatus().toString());
            r.setMessage(n.getMessage());
            r.setCourseTitle(n.getApplication().getCourse().getCourseTitle());
            r.setUniversityName(n.getApplication().getCourse().getUniversity().getName());
            r.setStudentUsername(n.getApplication().getApplicant().getUsername());
            r.setCreationDate(n.getApplication().getCreationDate());
            records.add(r);
        }
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), gson.toJson(records));
    }

    @Override
    public ApplicationNotification getApplicationNotification(String username, LocalDateTime timestamp) throws DAOException {
        getAll();
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
        saveToFile();
    }

    @Override
    public List<ApplicationNotification> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        String json = JsonUtil.readFile(file);

        Type listType = new TypeToken<List<JsonRecords.ApplicationNotificationRecord>>() {}.getType();
        List<JsonRecords.ApplicationNotificationRecord> records = gson.fromJson(json, listType);

        if (records != null) {
            for (JsonRecords.ApplicationNotificationRecord r : records) {
                try {
                    User user = JSONUserDAO.getInstance().getUser(r.getUsername());
                    Application application = JSONApplicationDAO.getInstance().getApplication(
                            r.getCourseTitle(), r.getUniversityName(), r.getStudentUsername(), r.getCreationDate());
                    NotificationStatus status = NotificationStatus.fromString(r.getStatus());

                    ApplicationNotification notification = new ApplicationNotification(r.getMessage(), user, application, r.getTimestamp(), status);
                    cache.add(notification);
                } catch (UserNotFoundException e) {
                    logger.log(Level.SEVERE, "User not found", e);
                }
            }
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void update(ApplicationNotification item) throws DAOException {
        cache.removeIf(n -> n.getSender().getUsername().equals(item.getSender().getUsername()) && n.getTimestamp().equals(item.getTimestamp()));
        cache.add(item);
        saveToFile();
    }

    @Override
    public void delete(ApplicationNotification item) throws DAOException {
        cache.removeIf(n -> n.getSender().getUsername().equals(item.getSender().getUsername()) && n.getTimestamp().equals(item.getTimestamp()));
        saveToFile();
    }
}