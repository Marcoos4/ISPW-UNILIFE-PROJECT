package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.UniversityEmployeeDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.UniversityEmployee;

import java.util.ArrayList;
import java.util.List;

public class DemoUniversityEmployeeDAO implements UniversityEmployeeDAO {

    private static DemoUniversityEmployeeDAO instance = null;
    private final List<UniversityEmployee> cache = new ArrayList<>();

    private DemoUniversityEmployeeDAO() {}

    public static DemoUniversityEmployeeDAO getInstance() {
        if (instance == null) {
            instance = new DemoUniversityEmployeeDAO();
        }
        return instance;
    }

    @Override
    public List<UniversityEmployee> getAll() throws DAOException {
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(UniversityEmployee item) throws DAOException {
        cache.add(item);
    }

    @Override
    public void update(UniversityEmployee item) throws DAOException {
        cache.removeIf(ue -> ue.getUsername().equals(item.getUsername()));
        cache.add(item);
    }

    @Override
    public void delete(UniversityEmployee item) throws DAOException {
        cache.removeIf(ue -> ue.getUsername().equals(item.getUsername()));
    }
}
