package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.StudentDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Student;

import java.util.ArrayList;
import java.util.List;

public class DemoStudentDAO implements StudentDAO {

    private static DemoStudentDAO instance = null;
    private final List<Student> cache = new ArrayList<>();

    private DemoStudentDAO() {}

    public static DemoStudentDAO getInstance() {
        if (instance == null) {
            instance = new DemoStudentDAO();
        }
        return instance;
    }

    @Override
    public Student getStudent(String username) throws UserNotFoundException, DAOException {
        for (Student student : cache) {
            if (student.getUsername().equals(username)) {
                return student;
            }
        }
        throw new UserNotFoundException();
    }

    @Override
    public List<Student> getAll() throws DAOException {
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Student item) throws DAOException {
        cache.add(item);
    }

    @Override
    public void update(Student item) throws DAOException {
        cache.removeIf(s -> s.getUsername().equals(item.getUsername()));
        cache.add(item);
    }

    @Override
    public void delete(Student item) throws DAOException {
        cache.removeIf(s -> s.getUsername().equals(item.getUsername()));
    }
}
