package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.StudentDAO;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.Student;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONStudentDAO implements StudentDAO {

    private static final String FILE_NAME = "students.json";
    private static JSONStudentDAO instance = null;
    private final List<Student> cache = new ArrayList<>();

    private JSONStudentDAO() throws DAOException {
        try {
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static synchronized JSONStudentDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new JSONStudentDAO();
        }
        return instance;
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        List<JsonRecords.StudentRecord> records = new ArrayList<>();
        for (Student s : cache) {
            JsonRecords.StudentRecord r = new JsonRecords.StudentRecord();
            r.setUsername(s.getUsername());
            r.setName(s.getName());
            r.setSurname(s.getSurname());
            r.setPassword(s.getPassword());
            r.setBudget(s.getBudget());
            records.add(r);
        }
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), gson.toJson(records));
    }

    @Override
    public Student getStudent(String username) throws UserNotFoundException, DAOException {
        if (cache.isEmpty()) {
            getAll();
        }
        for (Student student : cache) {
            if (student.getUsername().equals(username)) {
                return student;
            }
        }
        throw new UserNotFoundException();
    }

    @Override
    public List<Student> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        String json = JsonUtil.readFile(file);

        Type listType = new TypeToken<List<JsonRecords.StudentRecord>>() {}.getType();
        List<JsonRecords.StudentRecord> records = gson.fromJson(json, listType);

        if (records != null) {
            for (JsonRecords.StudentRecord r : records) {
                Student student = new Student(r.getUsername(), r.getName(), r.getSurname(), r.getPassword());
                student.setBudget(r.getBudget());

                List<Course> courses = JSONInterestedStudentDAO.getInstance().getInterestedCourse(r.getUsername());
                student.updateStarredCourses(courses);

                List<CourseTags> interests = JSONStudentInterestDAO.getInstance().getStudentInterests(r.getUsername());
                student.updateInterests(interests);

                cache.add(student);
            }
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Student item) throws DAOException {
        for (Course course : item.getStarredCourses()) {
            JSONInterestedStudentDAO.getInstance().insert(item, course);
        }
        for (CourseTags courseTags : item.getInterests()) {
            JSONStudentInterestDAO.getInstance().insert(courseTags, item);
        }
        cache.add(item);
        saveToFile();
    }

    @Override
    public void update(Student item) throws DAOException {
        cache.removeIf(s -> s.getUsername().equals(item.getUsername()));
        cache.add(item);
        saveToFile();
    }

    @Override
    public void delete(Student item) throws DAOException {
        cache.removeIf(s -> s.getUsername().equals(item.getUsername()));
        saveToFile();
    }
}