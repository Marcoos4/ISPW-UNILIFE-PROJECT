package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.TextRequirementDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.admission.TextRequirement;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONTextRequirementDAO implements TextRequirementDAO {

    private static final Logger logger = Logger.getLogger(JSONTextRequirementDAO.class.getName());
    private static final String FILE_NAME = "text_requirements.json";
    private static JSONTextRequirementDAO instance = null;
    private final List<JsonRecords.TextRequirementRecord> records = new ArrayList<>();

    private JSONTextRequirementDAO() {
        loadFromFile();
    }

    public static synchronized JSONTextRequirementDAO getInstance() {
        if (instance == null) {
            instance = new JSONTextRequirementDAO();
        }
        return instance;
    }

    private void loadFromFile() {
        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return;

        String json = JsonUtil.readFile(file);
        Type listType = new TypeToken<List<JsonRecords.TextRequirementRecord>>() {}.getType();
        List<JsonRecords.TextRequirementRecord> loaded = gson.fromJson(json, listType);
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
    public List<TextRequirement> getTextRequirements(String courseTitle, String universityName){
        List<TextRequirement> results = new ArrayList<>();
        for (JsonRecords.TextRequirementRecord r : records) {
            if (r.getCourseTitle().equals(courseTitle) && r.getUniversityName().equals(universityName)) {
                TextRequirement req = new TextRequirement(
                        r.getName(),
                        r.getLabel(),
                        r.getDescription(),
                        r.getMinChar(),
                        r.getMaxChar()
                );
                results.add(req);
            }
        }
        return results;
    }

    public void insert(TextRequirement item, Course course) {
        JsonRecords.TextRequirementRecord r = new JsonRecords.TextRequirementRecord();
        r.setCourseTitle(course.getCourseTitle());
        r.setUniversityName(course.getUniversity().getName());
        r.setName(item.getName());
        r.setMinChar(item.getMinChars());
        r.setMaxChar(item.getMaxChars());
        r.setLabel(item.getLabel());
        r.setDescription(item.getDescription());
        records.add(r);
        saveToFile();
    }

    public void update(TextRequirement item, Course course){
        delete(item, course);
        insert(item, course);
    }

    public void delete(TextRequirement item, Course course) {
        records.removeIf(r -> r.getCourseTitle().equals(course.getCourseTitle())
                && r.getUniversityName().equals(course.getUniversity().getName())
                && r.getName().equals(item.getName()));
        saveToFile();
    }

    @Override
    public List<TextRequirement> getAll() throws DAOException {
        logger.log(Level.WARNING, "Method getAll() not implemented");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insert(TextRequirement item) throws DAOException {
        logger.log(Level.WARNING, "Method insert(TextRequirement) not implemented. Use insert(item, course).");
        throw new DAOException("Use insert(item, course) instead");
    }

    @Override
    public void update(TextRequirement item) throws DAOException {
        logger.log(Level.WARNING, "Method update(TextRequirement) not implemented. Use update(item, course).");
        throw new DAOException("Use update(item, course) instead");
    }

    @Override
    public void delete(TextRequirement item) throws DAOException {
        logger.log(Level.WARNING, "Method delete(TextRequirement) not implemented. Use delete(item, course).");
        throw new DAOException("Use delete(item, course) instead");
    }
}