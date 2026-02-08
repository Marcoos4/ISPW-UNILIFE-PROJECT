package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.DocumentRequirementDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.admission.DocumentRequirement;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONDocumentRequirementDAO implements DocumentRequirementDAO {

    private static final String FILE_NAME = "document_requirements.json";
    private static JSONDocumentRequirementDAO instance = null;
    private final List<JsonRecords.DocumentRequirementRecord> records = new ArrayList<>();

    private JSONDocumentRequirementDAO() {
        loadFromFile();
    }

    public static synchronized JSONDocumentRequirementDAO getInstance() {
        if (instance == null) {
            instance = new JSONDocumentRequirementDAO();
        }
        return instance;
    }

    private void loadFromFile() {
        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return;

        String json = JsonUtil.readFile(file);
        Type listType = new TypeToken<List<JsonRecords.DocumentRequirementRecord>>() {}.getType();
        List<JsonRecords.DocumentRequirementRecord> loaded = gson.fromJson(json, listType);
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
    public List<DocumentRequirement> getDocumentRequirements(String courseTitle, String universityName) throws DAOException {
        List<DocumentRequirement> results = new ArrayList<>();
        for (JsonRecords.DocumentRequirementRecord r : records) {
            if (r.getCourseTitle().equals(courseTitle) && r.getUniversityName().equals(universityName)) {
                DocumentRequirement req = new DocumentRequirement(
                        r.getName(),
                        r.getLabel(),
                        r.getDescription(),
                        r.getAllowedExtension(),
                        (long) r.getMaxSize(),
                        r.isCertificate()
                );
                results.add(req);
            }
        }
        return results;
    }

    public void insert(DocumentRequirement item, Course course) {
        JsonRecords.DocumentRequirementRecord r = new JsonRecords.DocumentRequirementRecord();
        r.setCourseTitle(course.getCourseTitle());
        r.setUniversityName(course.getUniversity().getName());
        r.setName(item.getName());
        r.setMaxSize(item.getMaxSizeMB());
        r.setAllowedExtension(item.getAllowedExtension());
        r.setCertificate(item.isCertificate());
        r.setLabel(item.getLabel());
        r.setDescription(item.getDescription());
        records.add(r);
        saveToFile();
    }

    public void update(DocumentRequirement item, Course course) {
        delete(item, course);
        insert(item, course);
    }

    public void delete(DocumentRequirement item, Course course){
        records.removeIf(r -> r.getCourseTitle().equals(course.getCourseTitle())
                && r.getUniversityName().equals(course.getUniversity().getName())
                && r.getName().equals(item.getName()));
        saveToFile();
    }

    @Override
    public List<DocumentRequirement> getAll() throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insert(DocumentRequirement item) throws DAOException {
        throw new DAOException("Use insert(item, course) instead");
    }

    @Override
    public void update(DocumentRequirement item) throws DAOException {
        throw new DAOException("Use update(item, course) instead");
    }

    @Override
    public void delete(DocumentRequirement item) throws DAOException {
        throw new DAOException("Use delete(item, course) instead");
    }
}