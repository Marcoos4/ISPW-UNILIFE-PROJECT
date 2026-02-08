package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.ApplicationDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.admission.Application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DemoApplicationDAO implements ApplicationDAO {

    private static DemoApplicationDAO instance = null;
    private final List<Application> cache = new ArrayList<>();

    private DemoApplicationDAO() {}

    public static DemoApplicationDAO getInstance() {
        if (instance == null) {
            instance = new DemoApplicationDAO();
        }
        return instance;
    }

    @Override
    public List<Application> getApplications(String username) throws DAOException {
        List<Application> res = new ArrayList<>();
        for (Application application : cache) {
            if (application.getApplicant().getUsername().equals(username)) {
                res.add(application);
            }
        }
        return res;
    }

    @Override
    public Application getApplication(String courseTitle, String universityName, String studentUsername, LocalDateTime creationDate) throws DAOException {
        for (Application application : cache) {
            if (application.getApplicant().getUsername().equals(studentUsername)
                    && application.getCourse().getCourseTitle().equals(courseTitle)
                    && application.getCourse().getUniversity().getName().equals(universityName)
                    && application.getCreationDate().equals(creationDate)) {
                return application;
            }
        }
        return null;
    }

    @Override
    public List<Application> getAll() throws DAOException {
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Application item) throws DAOException {
        cache.add(item);
    }

    @Override
    public void update(Application item) throws DAOException {
        cache.removeIf(a -> a.equals(item));
        cache.add(item);
    }

    @Override
    public void delete(Application item) throws DAOException {
        cache.removeIf(a -> a.equals(item));
    }
}
