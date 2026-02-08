package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.ApplicationItemDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.admission.Application;
import it.ispw.unilife.model.admission.ApplicationItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoApplicationItemDAO implements ApplicationItemDAO {

    private static DemoApplicationItemDAO instance = null;
    // Chiave: courseTitle|universityName|studentUsername|creationDate
    private final Map<String, List<ApplicationItem>> cache = new HashMap<>();

    private DemoApplicationItemDAO() {}

    public static DemoApplicationItemDAO getInstance() {
        if (instance == null) {
            instance = new DemoApplicationItemDAO();
        }
        return instance;
    }

    private String buildKey(String courseTitle, String universityName, String studentUsername, LocalDateTime creationDate) {
        return courseTitle + "|" + universityName + "|" + studentUsername + "|" + creationDate.toString();
    }

    private String buildKey(Application application) {
        return buildKey(
                application.getCourse().getCourseTitle(),
                application.getCourse().getUniversity().getName(),
                application.getApplicant().getUsername(),
                application.getCreationDate()
        );
    }

    @Override
    public List<ApplicationItem> getItems(String courseTitle, String universityName, String studentUsername, LocalDateTime creationDate) throws DAOException {
        String key = buildKey(courseTitle, universityName, studentUsername, creationDate);
        return new ArrayList<>(cache.getOrDefault(key, new ArrayList<>()));
    }

    @Override
    public void insert(ApplicationItem item, Application application) throws DAOException {
        String key = buildKey(application);
        cache.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
    }

    @Override
    public void update(ApplicationItem item, Application application) throws DAOException {
        delete(item, application);
        insert(item, application);
    }

    @Override
    public void delete(ApplicationItem item, Application application) throws DAOException {
        String key = buildKey(application);
        List<ApplicationItem> items = cache.get(key);
        if (items != null) {
            items.removeIf(a -> a.equals(item));
        }
    }

    @Override
    public List<ApplicationItem> getAll() throws DAOException {
        return List.of();
    }

    @Override
    public void insert(ApplicationItem item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(ApplicationItem item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(ApplicationItem item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
