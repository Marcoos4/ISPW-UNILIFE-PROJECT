package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.CourseDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;

import java.util.ArrayList;
import java.util.List;

public class DemoCourseDAO implements CourseDAO {

    private static DemoCourseDAO instance = null;
    private final List<Course> cache = new ArrayList<>();

    private DemoCourseDAO() {}

    public static DemoCourseDAO getInstance() {
        if (instance == null) {
            instance = new DemoCourseDAO();
        }
        return instance;
    }

    @Override
    public Course getCourse(String courseTitle, String universityName) throws DAOException {
        for (Course course : cache) {
            if (course.getCourseTitle().equals(courseTitle) && course.getUniversity().getName().equals(universityName)) {
                return course;
            }
        }
        throw new DAOException("Course not found");
    }

    @Override
    public List<Course> getAll() throws DAOException {
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Course item) throws DAOException {
        cache.add(item);
    }

    @Override
    public void update(Course item) throws DAOException {
        cache.removeIf(c -> c.getCourseTitle().equals(item.getCourseTitle()) && c.getUniversity().getName().equals(item.getUniversity().getName()));
        cache.add(item);
    }

    @Override
    public void delete(Course item) throws DAOException {
        cache.removeIf(c -> c.getCourseTitle().equals(item.getCourseTitle()) && c.getUniversity().getName().equals(item.getUniversity().getName()));
    }
}
