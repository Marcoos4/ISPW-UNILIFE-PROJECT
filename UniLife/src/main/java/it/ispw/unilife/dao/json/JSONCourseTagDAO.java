package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.CourseTagDAO;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONCourseTagDAO implements CourseTagDAO {

    private static final String FILE_NAME = "course_tags.json";
    private static JSONCourseTagDAO instance = null;
    private final List<JsonRecords.CourseTagRecord> records = new ArrayList<>();

    private JSONCourseTagDAO() {
        loadFromFile();
    }

    public static synchronized JSONCourseTagDAO getInstance() {
        if (instance == null) {
            instance = new JSONCourseTagDAO();
        }
        return instance;
    }

    private void loadFromFile() {
        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return;

        String json = JsonUtil.readFile(file);
        Type listType = new TypeToken<List<JsonRecords.CourseTagRecord>>() {}.getType();
        List<JsonRecords.CourseTagRecord> loaded = gson.fromJson(json, listType);
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
    public List<CourseTags> getCourseTags(String courseName, String universityName) throws DAOException {
        List<CourseTags> results = new ArrayList<>();
        for (JsonRecords.CourseTagRecord r : records) {
            if (r.getCourseTitle().equals(courseName) && r.getUniversityName().equals(universityName)) {
                results.add(CourseTags.fromString(r.getTag()));
            }
        }
        return results;
    }

    public void insert(CourseTags item, Course course) {
        JsonRecords.CourseTagRecord r = new JsonRecords.CourseTagRecord();
        r.setCourseTitle(course.getCourseTitle());
        r.setUniversityName(course.getUniversity().getName());
        r.setTag(item.toString());
        records.add(r);
        saveToFile();
    }

    public void update(CourseTags item, Course course) {
        delete(item, course);
        insert(item, course);
    }

    public void delete(CourseTags item, Course course) {
        records.removeIf(r -> r.getCourseTitle().equals(course.getCourseTitle())
                && r.getUniversityName().equals(course.getUniversity().getName())
                && r.getTag().equals(item.toString()));
        saveToFile();
    }

    @Override
    public List<CourseTags> getAll() throws DAOException {
        return new ArrayList<>();
    }

    @Override
    public void insert(CourseTags item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(CourseTags item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(CourseTags item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}