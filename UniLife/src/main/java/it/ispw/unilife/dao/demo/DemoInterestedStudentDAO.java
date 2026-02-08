package it.ispw.unilife.dao.demo;

import it.ispw.unilife.controller.CourseDiscoveryAndApplication;
import it.ispw.unilife.dao.InterestedStudentDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoInterestedStudentDAO implements InterestedStudentDAO {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger((CourseDiscoveryAndApplication.class.getName()));

    private static DemoInterestedStudentDAO instance = null;
    // courseKey -> lista studenti
    private final Map<String, List<Student>> courseToStudents = new HashMap<>();
    // username -> lista corsi
    private final Map<String, List<Course>> studentToCourses = new HashMap<>();

    private DemoInterestedStudentDAO() {}

    public static DemoInterestedStudentDAO getInstance() {
        if (instance == null) {
            instance = new DemoInterestedStudentDAO();
        }
        return instance;
    }

    private String buildCourseKey(String courseTitle, String universityName) {
        return courseTitle + "|" + universityName;
    }

    @Override
    public List<Student> getInterestedStudents(String courseTitle, String universityName) throws DAOException {
        String key = buildCourseKey(courseTitle, universityName);
        return new ArrayList<>(courseToStudents.getOrDefault(key, new ArrayList<>()));
    }

    @Override
    public List<Course> getInterestedCourse(String username) throws DAOException {
        return new ArrayList<>(studentToCourses.getOrDefault(username, new ArrayList<>()));
    }

    public void insert(Student item, Course course) {
        String courseKey = buildCourseKey(course.getCourseTitle(), course.getUniversity().getName());
        courseToStudents.computeIfAbsent(courseKey, k -> new ArrayList<>()).add(item);
        studentToCourses.computeIfAbsent(item.getUsername(), k -> new ArrayList<>()).add(course);
    }

    public void update(Student item, Course course){
        delete(item, course);
        insert(item, course);
    }

    public void delete(Student item, Course course)  {
        String courseKey = buildCourseKey(course.getCourseTitle(), course.getUniversity().getName());
        List<Student> students = courseToStudents.get(courseKey);
        if (students != null) {
            students.removeIf(s -> s.getUsername().equals(item.getUsername()));
        }
        List<Course> courses = studentToCourses.get(item.getUsername());
        if (courses != null) {
            courses.removeIf(c -> c.getCourseTitle().equals(course.getCourseTitle()) &&
                    c.getUniversity().getName().equals(course.getUniversity().getName()));
        }
    }

    @Override
    public List<Student> getAll() throws DAOException {
        return List.of();
    }

    @Override
    public void insert(Student item) throws DAOException {
        LOGGER.info("Insert not implemented");
    }

    @Override
    public void update(Student item) throws DAOException {
        LOGGER.info("Update not implemented");
    }

    @Override
    public void delete(Student item) throws DAOException {
        LOGGER.info("Delete not implemented");
    }
}
