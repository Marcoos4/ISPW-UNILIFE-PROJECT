package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.UniversityDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.University;

import java.util.ArrayList;
import java.util.List;

public class DemoUniversityDAO implements UniversityDAO {

    private static DemoUniversityDAO instance = null;
    private final List<University> cache = new ArrayList<>();

    private DemoUniversityDAO() {
        cache.add(new University("University of Oxford", "", 4,1600.0));
        cache.add(new University("Sapienza Università di Roma", "Italy",134, 850.0));
        cache.add(new University("Università di Bologna" ,"Bologna", 160,  900.0));
        cache.add(new University("Massachusetts Institute of Technology (MIT)","Cambridge, MA",  1,2500.0));
        cache.add(new University("University of Oxford","Oxford", 4, 1600.0));
        cache.add(new University("Università degli Studi di Napoli Federico II","Napoli",350, 700.0));
    }

    public static DemoUniversityDAO getInstance() {
        if (instance == null) {
            instance = new DemoUniversityDAO();
        }
        return instance;
    }

    @Override
    public University getUniversity(String uniName) throws DAOException {
        for (University university : cache) {
            if (university.getName().equals(uniName)) {
                return university;
            }
        }
        return null;
    }

    @Override
    public List<University> getAll() throws DAOException {
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(University item) throws DAOException {
        cache.add(item);
    }

    @Override
    public void update(University item) throws DAOException {
        cache.removeIf(u -> u.getName().equals(item.getName()));
        cache.add(item);
    }

    @Override
    public void delete(University item) throws DAOException {
        cache.removeIf(u -> u.getName().equals(item.getName()));
    }
}
