package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.TextRequirementDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.admission.TextRequirement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoTextRequirementDAO implements TextRequirementDAO {

    private static DemoTextRequirementDAO instance = null;
    private final Map<String, List<TextRequirement>> cache = new HashMap<>();

    private DemoTextRequirementDAO() {}

    public static DemoTextRequirementDAO getInstance() {
        if (instance == null) {
            instance = new DemoTextRequirementDAO();
        }
        return instance;
    }

    private String buildKey(String courseTitle, String universityName) {
        return courseTitle + "|" + universityName;
    }

    @Override
    public List<TextRequirement> getTextRequirements(String courseTitle, String universityName) throws DAOException {
        String key = buildKey(courseTitle, universityName);
        return new ArrayList<>(cache.getOrDefault(key, new ArrayList<>()));
    }

    public void insert(TextRequirement item, Course course) {
        String key = buildKey(course.getCourseTitle(), course.getUniversity().getName());
        cache.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
    }

    public void update(TextRequirement item, Course course) {
        delete(item, course);
        insert(item, course);
    }

    public void delete(TextRequirement item, Course course) {
        String key = buildKey(course.getCourseTitle(), course.getUniversity().getName());
        List<TextRequirement> reqs = cache.get(key);
        if (reqs != null) {
            reqs.removeIf(r -> r.getName().equals(item.getName()));
        }
    }

    @Override
    public List<TextRequirement> getAll() throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insert(TextRequirement item) throws DAOException {
        throw new DAOException("Use insert(item, course) instead");
    }

    @Override
    public void update(TextRequirement item) throws DAOException {
        throw new DAOException("Use update(item, course) instead");
    }

    @Override
    public void delete(TextRequirement item) throws DAOException {
        throw new DAOException("Use delete(item, course) instead");
    }
}
