package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.StudentInterestDAO;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Student;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONStudentInterestDAO implements StudentInterestDAO {

    private static final Logger logger = Logger.getLogger(JSONStudentInterestDAO.class.getName());
    private static final String FILE_NAME = "student_interests.json";
    private static JSONStudentInterestDAO instance = null;
    private final List<JsonRecords.StudentInterestRecord> records = new ArrayList<>();

    private JSONStudentInterestDAO() {
        loadFromFile();
    }

    public static synchronized JSONStudentInterestDAO getInstance() {
        if (instance == null) {
            instance = new JSONStudentInterestDAO();
        }
        return instance;
    }

    private void loadFromFile() {
        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return;

        String json = JsonUtil.readFile(file);
        Type listType = new TypeToken<List<JsonRecords.StudentInterestRecord>>() {}.getType();
        List<JsonRecords.StudentInterestRecord> loaded = gson.fromJson(json, listType);
        if (loaded != null) {
            records.clear();
            records.addAll(loaded);
        }
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        String json = gson.toJson(records);
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), json);
    }

    @Override
    public List<CourseTags> getStudentInterests(String studentUsername) {
        List<CourseTags> results = new ArrayList<>();
        for (JsonRecords.StudentInterestRecord r : records) {
            if (r.getStudentUsername().equals(studentUsername)) {
                CourseTags tag = CourseTags.fromString(r.getTag());
                if (tag != null) {
                    results.add(tag);
                }
            }
        }
        return results;
    }

    public void insert(CourseTags item, Student student) {
        JsonRecords.StudentInterestRecord r = new JsonRecords.StudentInterestRecord();
        r.setStudentUsername(student.getUsername());
        r.setTag(item.toString());
        records.add(r);
        saveToFile();
    }

    public void update(CourseTags item, Student student) {
        delete(item, student);
        insert(item, student);
    }

    public void delete(CourseTags item, Student student){
        records.removeIf(r -> r.getStudentUsername().equals(student.getUsername()) && r.getTag().equals(item.toString()));
        saveToFile();
    }

    @Override
    public List<CourseTags> getAll() throws DAOException {
        logger.log(Level.WARNING, "Method getAll() not implemented");
        return new ArrayList<>();
    }

    @Override
    public void insert(CourseTags item) throws DAOException {
        logger.log(Level.WARNING, "Method insert(CourseTags) not implemented");
    }

    @Override
    public void update(CourseTags item) throws DAOException {
        logger.log(Level.WARNING, "Method update(CourseTags) not implemented");
    }

    @Override
    public void delete(CourseTags item) throws DAOException {
        logger.log(Level.WARNING, "Method delete(CourseTags) not implemented");
    }
}