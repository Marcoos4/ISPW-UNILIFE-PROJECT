package it.ispw.unilife.dao.demo;

import it.ispw.unilife.controller.CourseDiscoveryAndApplication;
import it.ispw.unilife.dao.CourseTagDAO;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoCourseTagDAO implements CourseTagDAO {

    private static final String ISEMPTY = "Is Empty";
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger((CourseDiscoveryAndApplication.class.getName()));

    private static DemoCourseTagDAO instance = null;
    private final Map<String, List<CourseTags>> cache = new HashMap<>();

    private DemoCourseTagDAO() {}

    public static DemoCourseTagDAO getInstance() {
        if (instance == null) {
            instance = new DemoCourseTagDAO();
        }
        return instance;
    }

    private String buildKey(String courseTitle, String universityName) {
        return courseTitle + "|" + universityName;
    }

    @Override
    public List<CourseTags> getCourseTags(String courseName, String universityName) throws DAOException {
        String key = buildKey(courseName, universityName);
        return new ArrayList<>(cache.getOrDefault(key, new ArrayList<>()));
    }

    public void insert(CourseTags item, Course course){
        String key = buildKey(course.getCourseTitle(), course.getUniversity().getName());
        cache.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
    }

    public void update(CourseTags item, Course course) {
        delete(item, course);
        insert(item, course);
    }

    public void delete(CourseTags item, Course course) {
        String key = buildKey(course.getCourseTitle(), course.getUniversity().getName());
        List<CourseTags> tags = cache.get(key);
        if (tags != null) {
            tags.remove(item);
        }
    }

    @Override
    public List<CourseTags> getAll() throws DAOException {
        return List.of();
    }

    @Override
    public void insert(CourseTags item) throws DAOException {
        LOGGER.info(ISEMPTY);
    }

    @Override
    public void update(CourseTags item) throws DAOException {
        LOGGER.info(ISEMPTY);
    }

    @Override
    public void delete(CourseTags item) throws DAOException {
        LOGGER.info(ISEMPTY);
    }
}
