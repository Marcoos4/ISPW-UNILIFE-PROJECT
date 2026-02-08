package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.LessonNotificationDAO;
import it.ispw.unilife.enums.NotificationStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Lesson;
import it.ispw.unilife.model.Tutor;
import it.ispw.unilife.model.notification.LessonNotification;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONLessonNotificationDAO implements LessonNotificationDAO {

    private static final Logger logger = Logger.getLogger(JSONLessonNotificationDAO.class.getName());
    private static final String FILE_NAME = "lesson_notifications.json";
    private static JSONLessonNotificationDAO instance = null;
    private final List<LessonNotification> cache = new ArrayList<>();

    private JSONLessonNotificationDAO() throws DAOException {
        try {
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static synchronized JSONLessonNotificationDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new JSONLessonNotificationDAO();
        }
        return instance;
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        List<JsonRecords.LessonNotificationRecord> records = new ArrayList<>();
        for (LessonNotification n : cache) {
            JsonRecords.LessonNotificationRecord r = new JsonRecords.LessonNotificationRecord();
            r.setUsername(n.getSender().getUsername());
            r.setTimestamp(n.getTimestamp());
            r.setStatus(n.getStatus().toString());
            r.setMessage(n.getMessage());
            r.setStart(n.getLesson().getStartTime());
            r.setTutorUsername(n.getLesson().getTutor().getUsername());
            records.add(r);
        }
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), gson.toJson(records));
    }

    @Override
    public LessonNotification getLessonNotification(String username, LocalDateTime timestamp) throws DAOException {
        getAll();
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
        saveToFile();
    }

    @Override
    public List<LessonNotification> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        String json = JsonUtil.readFile(file);

        Type listType = new TypeToken<List<JsonRecords.LessonNotificationRecord>>() {}.getType();
        List<JsonRecords.LessonNotificationRecord> records = gson.fromJson(json, listType);

        if (records != null) {
            for (JsonRecords.LessonNotificationRecord r : records) {
                try {
                    Tutor tutor = JSONTutorDAO.getInstance().getTutor(r.getTutorUsername());
                    Lesson lesson = JSONLessonDAO.getInstance().getLesson(r.getTutorUsername(), r.getStart());
                    NotificationStatus status = NotificationStatus.fromString(r.getStatus());

                    if (lesson != null) {
                        LessonNotification notification = new LessonNotification(r.getMessage(), tutor, lesson, r.getTimestamp(), status);
                        cache.add(notification);
                    }
                } catch (UserNotFoundException e) {
                    logger.log(Level.SEVERE, "Tutor not found", e);
                }
            }
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void update(LessonNotification item) throws DAOException {
        cache.removeIf(n -> n.getSender().getUsername().equals(item.getSender().getUsername()) && n.getTimestamp().equals(item.getTimestamp()));
        cache.add(item);
        saveToFile();
    }

    @Override
    public void delete(LessonNotification item) throws DAOException {
        cache.removeIf(n -> n.getSender().getUsername().equals(item.getSender().getUsername()) && n.getTimestamp().equals(item.getTimestamp()));
        saveToFile();
    }
}