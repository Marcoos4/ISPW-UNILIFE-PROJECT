package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.TutorDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Tutor;

import java.util.ArrayList;
import java.util.List;

public class DemoTutorDAO implements TutorDAO {

    private static DemoTutorDAO instance = null;
    private final List<Tutor> cache = new ArrayList<>();

    private DemoTutorDAO() {}

    public static DemoTutorDAO getInstance() {
        if (instance == null) {
            instance = new DemoTutorDAO();
        }
        return instance;
    }

    @Override
    public Tutor getTutor(String username) throws UserNotFoundException, DAOException {
        for (Tutor tutor : cache) {
            if (tutor.getUsername().equalsIgnoreCase(username)) {
                return tutor;
            }
        }
        return null;
    }

    @Override
    public List<Tutor> getAll() throws DAOException {
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Tutor item) throws DAOException {
        cache.add(item);
    }

    @Override
    public void update(Tutor item) throws DAOException {
        cache.removeIf(t -> t.getUsername().equals(item.getUsername()));
        cache.add(item);
    }

    @Override
    public void delete(Tutor item) throws DAOException {
        cache.removeIf(t -> t.getUsername().equals(item.getUsername()));
    }
}
