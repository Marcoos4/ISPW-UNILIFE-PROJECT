package it.ispw.unilife.dao.json;

import it.ispw.unilife.dao.UserDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Student;
import it.ispw.unilife.model.Tutor;
import it.ispw.unilife.model.UniversityEmployee;
import it.ispw.unilife.model.User;

import java.util.ArrayList;
import java.util.List;

public class JSONUserDAO implements UserDAO {

    private static JSONUserDAO instance = null;
    private final List<User> cache = new ArrayList<>();

    private JSONUserDAO() throws DAOException {
        try {
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static synchronized JSONUserDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new JSONUserDAO();
        }
        return instance;
    }

    @Override
    public User getUser(String username) throws UserNotFoundException, DAOException {
        if (cache.isEmpty()) {
            getAll();
        }
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
            cache.addAll(JSONStudentDAO.getInstance().getAll());
            cache.addAll(JSONTutorDAO.getInstance().getAll());
            cache.addAll(JSONUniversityEmployeeDAO.getInstance().getAll());
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(User item) throws DAOException {
        switch (item.getRole()) {
            case TUTOR -> JSONTutorDAO.getInstance().insert((Tutor) item);
            case STUDENT -> JSONStudentDAO.getInstance().insert((Student) item);
            case UNIVERSITY_EMPLOYEE -> JSONUniversityEmployeeDAO.getInstance().insert((UniversityEmployee) item);
        }
        cache.add(item);
    }

    @Override
    public void update(User item) throws DAOException {
        switch (item.getRole()) {
            case TUTOR -> JSONTutorDAO.getInstance().update((Tutor) item);
            case STUDENT -> JSONStudentDAO.getInstance().update((Student) item);
            case UNIVERSITY_EMPLOYEE -> JSONUniversityEmployeeDAO.getInstance().update((UniversityEmployee) item);
        }
        cache.removeIf(u -> u.getUsername().equals(item.getUsername()));
        cache.add(item);
    }

    @Override
    public void delete(User item) throws DAOException {
        switch (item.getRole()) {
            case TUTOR -> JSONTutorDAO.getInstance().delete((Tutor) item);
            case STUDENT -> JSONStudentDAO.getInstance().delete((Student) item);
            case UNIVERSITY_EMPLOYEE -> JSONUniversityEmployeeDAO.getInstance().delete((UniversityEmployee) item);
        }
        cache.removeIf(u -> u.getUsername().equals(item.getUsername()));
    }
}