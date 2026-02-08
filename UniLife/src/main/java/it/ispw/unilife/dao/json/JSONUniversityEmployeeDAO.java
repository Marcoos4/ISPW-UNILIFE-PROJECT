package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.UniversityEmployeeDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.UniversityEmployee;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONUniversityEmployeeDAO implements UniversityEmployeeDAO {

    private static final String FILE_NAME = "university_employees.json";
    private static JSONUniversityEmployeeDAO instance = null;
    private final List<UniversityEmployee> cache = new ArrayList<>();

    private JSONUniversityEmployeeDAO() throws DAOException {
        try {
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static synchronized JSONUniversityEmployeeDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new JSONUniversityEmployeeDAO();
        }
        return instance;
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        List<JsonRecords.UniversityEmployeeRecord> records = new ArrayList<>();
        for (UniversityEmployee ue : cache) {
            JsonRecords.UniversityEmployeeRecord r = new JsonRecords.UniversityEmployeeRecord();
            r.setUsername(ue.getUsername());
            r.setName(ue.getName());
            r.setSurname(ue.getSurname());
            r.setPassword(ue.getPassword());
            r.setUniversityName(ue.getUniversity() != null ? ue.getUniversity().getName() : null);
            records.add(r);
        }
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), gson.toJson(records));
    }

    @Override
    public List<UniversityEmployee> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        String json = JsonUtil.readFile(file);

        Type listType = new TypeToken<List<JsonRecords.UniversityEmployeeRecord>>() {}.getType();
        List<JsonRecords.UniversityEmployeeRecord> records = gson.fromJson(json, listType);

        if (records != null) {
            for (JsonRecords.UniversityEmployeeRecord r : records) {
                UniversityEmployee ue = new UniversityEmployee(r.getUsername(), r.getName(), r.getSurname(), r.getPassword());
                if (r.getUniversityName() != null) {
                    ue.setUniversity(JSONUniversityDAO.getInstance().getUniversity(r.getUniversityName()));
                }
                cache.add(ue);
            }
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(UniversityEmployee item) throws DAOException {
        cache.add(item);
        saveToFile();
    }

    @Override
    public void update(UniversityEmployee item) throws DAOException {
        cache.removeIf(ue -> ue.getUsername().equals(item.getUsername()));
        cache.add(item);
        saveToFile();
    }

    @Override
    public void delete(UniversityEmployee item) throws DAOException {
        cache.removeIf(ue -> ue.getUsername().equals(item.getUsername()));
        saveToFile();
    }
}