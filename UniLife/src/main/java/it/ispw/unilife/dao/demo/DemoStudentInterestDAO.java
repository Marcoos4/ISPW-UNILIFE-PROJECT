package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.StudentInterestDAO;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoStudentInterestDAO implements StudentInterestDAO {

    private static DemoStudentInterestDAO instance = null;
    private final Map<String, List<CourseTags>> cache = new HashMap<>();

    private DemoStudentInterestDAO() {}

    public static DemoStudentInterestDAO getInstance() {
        if (instance == null) {
            instance = new DemoStudentInterestDAO();
        }
        return instance;
    }

    @Override
    public List<CourseTags> getStudentInterests(String studentUsername) throws DAOException {
        return new ArrayList<>(cache.getOrDefault(studentUsername, new ArrayList<>()));
    }

    public void insert(CourseTags item, Student student){
        cache.computeIfAbsent(student.getUsername(), k -> new ArrayList<>()).add(item);
    }

    public void update(CourseTags item, Student student){
        delete(item, student);
        insert(item, student);
    }

    public void delete(CourseTags item, Student student) {
        List<CourseTags> tags = cache.get(student.getUsername());
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
