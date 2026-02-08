package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.LessonDAO;
import it.ispw.unilife.enums.LessonStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Lesson;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONLessonDAO implements LessonDAO {

    private static final Logger logger = Logger.getLogger(JSONLessonDAO.class.getName());
    private static final String FILE_NAME = "lessons.json";
    private static JSONLessonDAO instance = null;
    private final List<Lesson> cache = new ArrayList<>();

    private JSONLessonDAO() throws DAOException {
        try {
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static synchronized JSONLessonDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new JSONLessonDAO();
        }
        return instance;
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        List<JsonRecords.LessonRecord> records = new ArrayList<>();
        for (Lesson l : cache) {
            JsonRecords.LessonRecord r = new JsonRecords.LessonRecord();
            r.setSubject(l.getSubject());
            r.setPrice(l.getPricePerHour());
            r.setStart(l.getStartTime());
            r.setEnd(l.getEndTime());
            r.setDuration(l.getDurationInHours());
            r.setTutorUsername(l.getTutor().getUsername());
            r.setStatus(l.getStatus().toString());
            records.add(r);
        }
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), gson.toJson(records));
    }

    @Override
    public Lesson getLesson(String username, LocalDateTime creationDate) throws DAOException {
        if (cache.isEmpty()) {
            getAll();
        }
        LocalDateTime searchDate = creationDate.truncatedTo(ChronoUnit.SECONDS);
        for (Lesson lesson : cache) {
            LocalDateTime lessonDate = lesson.getStartTime().truncatedTo(ChronoUnit.SECONDS);
            if (lesson.getTutor().getUsername().equals(username) && lessonDate.equals(searchDate)) {
                return lesson;
            }
        }
        return null;
    }

    @Override
    public List<Lesson> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        String json = JsonUtil.readFile(file);

        Type listType = new TypeToken<List<JsonRecords.LessonRecord>>() {}.getType();
        List<JsonRecords.LessonRecord> records = gson.fromJson(json, listType);

        if (records != null) {
            for (JsonRecords.LessonRecord r : records) {
                try {
                    Lesson l = new Lesson(r.getSubject(), r.getStart(), r.getEnd(), r.getPrice(),
                            JSONTutorDAO.getInstance().getTutor(r.getTutorUsername()),
                            LessonStatus.fromString(r.getStatus()));
                    cache.add(l);
                } catch (UserNotFoundException e) {
                    logger.log(Level.SEVERE, "Tutor not found: {0}", r.getTutorUsername());
                }
            }
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Lesson item) throws DAOException {
        cache.add(item);
        saveToFile();
    }

    @Override
    public void update(Lesson item) throws DAOException {
        cache.removeIf(l ->
                l.getTutor().getUsername().equals(item.getTutor().getUsername()) &&
                        l.getStartTime().equals(item.getStartTime()));
        cache.add(item);
        saveToFile();
    }

    @Override
    public void delete(Lesson item) throws DAOException {
        cache.removeIf(l ->
                l.getTutor().getUsername().equals(item.getTutor().getUsername()) &&
                        l.getStartTime().equals(item.getStartTime()));
        saveToFile();
    }
}