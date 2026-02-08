package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.UserDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Student;
import it.ispw.unilife.model.Tutor;
import it.ispw.unilife.model.UniversityEmployee;
import it.ispw.unilife.model.User;

import java.util.ArrayList;
import java.util.List;

public class DemoUserDAO implements UserDAO {

    private static DemoUserDAO instance = null;
    private final List<User> cache = new ArrayList<>();

    private DemoUserDAO() {}

    public static DemoUserDAO getInstance() {
        if (instance == null) {
            instance = new DemoUserDAO();
        }
        return instance;
    }

    @Override
    public User getUser(String username) throws UserNotFoundException, DAOException {
        for (User u : cache) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        throw new UserNotFoundException();
    }

    @Override
    public List<User> getAll() throws DAOException {
        if (cache.isEmpty()) {
            cache.addAll(DemoStudentDAO.getInstance().getAll());
            cache.addAll(DemoTutorDAO.getInstance().getAll());
            cache.addAll(DemoUniversityEmployeeDAO.getInstance().getAll());
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(User item) throws DAOException {
        switch (item.getRole()) {
            case TUTOR -> DemoTutorDAO.getInstance().insert((Tutor) item);
            case STUDENT -> DemoStudentDAO.getInstance().insert((Student) item);
            case UNIVERSITY_EMPLOYEE -> DemoUniversityEmployeeDAO.getInstance().insert((UniversityEmployee) item);
        }
        cache.add(item);
    }

    @Override
    public void update(User item) throws DAOException {
        switch (item.getRole()) {
            case TUTOR -> DemoTutorDAO.getInstance().update((Tutor) item);
            case STUDENT -> DemoStudentDAO.getInstance().update((Student) item);
            case UNIVERSITY_EMPLOYEE -> DemoUniversityEmployeeDAO.getInstance().update((UniversityEmployee) item);
        }
        cache.removeIf(u -> u.getUsername().equals(item.getUsername()));
        cache.add(item);
    }

    @Override
    public void delete(User item) throws DAOException {
        switch (item.getRole()) {
            case TUTOR -> DemoTutorDAO.getInstance().delete((Tutor) item);
            case STUDENT -> DemoStudentDAO.getInstance().delete((Student) item);
            case UNIVERSITY_EMPLOYEE -> DemoUniversityEmployeeDAO.getInstance().delete((UniversityEmployee) item);
        }
        cache.removeIf(u -> u.getUsername().equals(item.getUsername()));
    }
}
