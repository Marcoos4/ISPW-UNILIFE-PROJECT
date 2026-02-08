package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.ApplicationItemDAO;
import it.ispw.unilife.enums.RequirementType;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.admission.Application;
import it.ispw.unilife.model.admission.ApplicationItem;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JSONApplicationItemDAO implements ApplicationItemDAO {

    private static final String FILE_NAME = "application_items.json";
    private static JSONApplicationItemDAO instance = null;
    private final List<JsonRecords.ApplicationItemRecord> records = new ArrayList<>();

    private JSONApplicationItemDAO() {
        loadFromFile();
    }

    public static synchronized JSONApplicationItemDAO getInstance() {
        if (instance == null) {
            instance = new JSONApplicationItemDAO();
        }
        return instance;
    }

    private void loadFromFile() {
        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return;

        String json = JsonUtil.readFile(file);
        Type listType = new TypeToken<List<JsonRecords.ApplicationItemRecord>>() {}.getType();
        List<JsonRecords.ApplicationItemRecord> loaded = gson.fromJson(json, listType);

        if (loaded != null) {
            records.clear();
            records.addAll(loaded);
        }
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        String json = gson.toJson(records);
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), json);
    }

    @Override
    public List<ApplicationItem> getItems(String courseTitle, String universityName, String studentUsername, LocalDateTime creationDate) throws DAOException {
        List<ApplicationItem> result = new ArrayList<>();
        for (JsonRecords.ApplicationItemRecord r : records) {
            if (r.getCourseTitle().equals(courseTitle) &&
                    r.getUniversityName().equals(universityName) &&
                    r.getStudentUsername().equals(studentUsername) &&
                    r.getCreationDate().equals(creationDate)) {

                ApplicationItem item = new ApplicationItem(
                        r.getRequirementName(),
                        RequirementType.fromString(r.getType()),
                        r.getText(),
                        JSONDocumentDAO.getInstance().getDocument(r.getDocument())
                );
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public void insert(ApplicationItem item, Application application) throws DAOException {
        JsonRecords.ApplicationItemRecord r = new JsonRecords.ApplicationItemRecord();
        r.setCourseTitle(application.getCourse().getCourseTitle());
        r.setUniversityName(application.getCourse().getUniversity().getName());
        r.setCreationDate(application.getCreationDate());
        r.setStudentUsername(application.getApplicant().getUsername());
        r.setRequirementName(item.getRequirementName());
        r.setType(item.getType().toString());
        r.setText(item.getTextContent());

        if (item.getDocumentContent() != null) {
            r.setDocument(item.getDocumentContent().getFileName());
        } else {
            r.setDocument(null);
        }

        records.add(r);
        saveToFile();
    }

    @Override
    public void update(ApplicationItem item, Application application) throws DAOException {
        delete(item, application);
        insert(item, application);
    }

    @Override
    public void delete(ApplicationItem item, Application application) throws DAOException {
        records.removeIf(r -> r.getCourseTitle().equals(application.getCourse().getCourseTitle())
                && r.getUniversityName().equals(application.getCourse().getUniversity().getName())
                && r.getStudentUsername().equals(application.getApplicant().getUsername())
                && r.getCreationDate().equals(application.getCreationDate())
                && r.getRequirementName().equals(item.getRequirementName()));
        saveToFile();
    }

    @Override
    public List<ApplicationItem> getAll() throws DAOException {
        return new ArrayList<>();
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