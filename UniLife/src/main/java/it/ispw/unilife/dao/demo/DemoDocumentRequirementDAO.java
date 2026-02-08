package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.DocumentRequirementDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.admission.DocumentRequirement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoDocumentRequirementDAO implements DocumentRequirementDAO {

    private static DemoDocumentRequirementDAO instance = null;
    private final Map<String, List<DocumentRequirement>> cache = new HashMap<>();

    private DemoDocumentRequirementDAO() {}

    public static DemoDocumentRequirementDAO getInstance() {
        if (instance == null) {
            instance = new DemoDocumentRequirementDAO();
        }
        return instance;
    }

    private String buildKey(String courseTitle, String universityName) {
        return courseTitle + "|" + universityName;
    }

    @Override
    public List<DocumentRequirement> getDocumentRequirements(String courseTitle, String universityName) throws DAOException {
        String key = buildKey(courseTitle, universityName);
        return new ArrayList<>(cache.getOrDefault(key, new ArrayList<>()));
    }

    public void insert(DocumentRequirement item, Course course) {
        String key = buildKey(course.getCourseTitle(), course.getUniversity().getName());
        cache.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
    }

    public void update(DocumentRequirement item, Course course){
        delete(item, course);
        insert(item, course);
    }

    public void delete(DocumentRequirement item, Course course){
        String key = buildKey(course.getCourseTitle(), course.getUniversity().getName());
        List<DocumentRequirement> reqs = cache.get(key);
        if (reqs != null) {
            reqs.removeIf(r -> r.getName().equals(item.getName()));
        }
    }

    @Override
    public List<DocumentRequirement> getAll() throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insert(DocumentRequirement item) throws DAOException {
        throw new DAOException("Use insert(item, course) instead");
    }

    @Override
    public void update(DocumentRequirement item) throws DAOException {
        throw new DAOException("Use update(item, course) instead");
    }

    @Override
    public void delete(DocumentRequirement item) throws DAOException {
        throw new DAOException("Use delete(item, course) instead");
    }
}
