package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.InterestedStudentDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.Student;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONInterestedStudentDAO implements InterestedStudentDAO {

    private static final Logger logger = Logger.getLogger(JSONInterestedStudentDAO.class.getName());
    private static final String FILE_NAME = "interested_students.json";
    private static JSONInterestedStudentDAO instance = null;
    private final List<JsonRecords.InterestedStudentRecord> records = new ArrayList<>();

    private JSONInterestedStudentDAO() {
        loadFromFile();
    }

    public static synchronized JSONInterestedStudentDAO getInstance() {
        if (instance == null) {
            instance = new JSONInterestedStudentDAO();
        }
        return instance;
    }

    private void loadFromFile() {
        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return;

        String json = JsonUtil.readFile(file);
        Type listType = new TypeToken<List<JsonRecords.InterestedStudentRecord>>() {}.getType();
        List<JsonRecords.InterestedStudentRecord> loaded = gson.fromJson(json, listType);
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
    public List<Student> getInterestedStudents(String courseTitle, String universityName) throws DAOException {
        List<Student> results = new ArrayList<>();
        for (JsonRecords.InterestedStudentRecord r : records) {
            if (r.getCourseName().equals(courseTitle) && r.getUniversityName().equals(universityName)) {
                try {
                    Student s = JSONStudentDAO.getInstance().getStudent(r.getUsername());
                    results.add(s);
                } catch (UserNotFoundException e) {
                    logger.log(Level.SEVERE, "Student not found: {0}", r.getUsername());
                }
            }
        }
        return results;
    }

    @Override
    public List<Course> getInterestedCourse(String username) throws DAOException {
        List<Course> results = new ArrayList<>();
        for (JsonRecords.InterestedStudentRecord r : records) {
            if (r.getUsername().equals(username)) {
                Course c = JSONCourseDAO.getInstance().getCourse(r.getCourseName(), r.getUniversityName());
                results.add(c);
            }
        }
        return results;
    }

    public void insert(Student item, Course course){
        JsonRecords.InterestedStudentRecord r = new JsonRecords.InterestedStudentRecord();
        r.setUsername(item.getUsername());
        r.setCourseName(course.getCourseTitle());
        r.setUniversityName(course.getUniversity().getName());
        records.add(r);
        saveToFile();
    }

    public void update(Student item, Course course){
        delete(item, course);
        insert(item, course);
    }

    public void delete(Student item, Course course){
        records.removeIf(r -> r.getUsername().equals(item.getUsername())
                && r.getCourseName().equals(course.getCourseTitle())
                && r.getUniversityName().equals(course.getUniversity().getName()));
        saveToFile();
    }

    @Override
    public List<Student> getAll() throws DAOException {
        return new ArrayList<>();
    }

    @Override
    public void insert(Student item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Student item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Student item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}