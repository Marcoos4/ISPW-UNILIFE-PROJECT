package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.UniversityDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.University;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONUniversityDAO implements UniversityDAO {

    private static final String FILE_NAME = "universities.json";
    private static JSONUniversityDAO instance = null;
    private final List<University> cache = new ArrayList<>();

    private JSONUniversityDAO() throws DAOException {
        try {
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static synchronized JSONUniversityDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new JSONUniversityDAO();
        }
        return instance;
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        List<JsonRecords.UniversityRecord> records = new ArrayList<>();
        for (University u : cache) {
            JsonRecords.UniversityRecord r = new JsonRecords.UniversityRecord();
            r.setName(u.getName());
            r.setLocation(u.getLocation());
            r.setRanking(u.getRanking());
            r.setLivingCost(u.getLivingCosts());
            records.add(r);
        }
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), gson.toJson(records));
    }

    @Override
    public University getUniversity(String uniName) throws DAOException {
        if (cache.isEmpty()) {
            getAll();
        }
        for (University university : cache) {
            if (university.getName().equals(uniName)) {
                return university;
            }
        }
        return null;
    }

    @Override
    public List<University> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        String json = JsonUtil.readFile(file);

        Type listType = new TypeToken<List<JsonRecords.UniversityRecord>>() {}.getType();
        List<JsonRecords.UniversityRecord> records = gson.fromJson(json, listType);

        if (records != null) {
            for (JsonRecords.UniversityRecord r : records) {
                University university = new University(r.getName(), r.getLocation(), r.getRanking(), r.getLivingCost());
                cache.add(university);
            }
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(University item) throws DAOException {
        cache.add(item);
        saveToFile();
    }

    @Override
    public void update(University item) throws DAOException {
        cache.removeIf(u -> u.getName().equals(item.getName()));
        cache.add(item);
        saveToFile();
    }

    @Override
    public void delete(University item) throws DAOException {
        cache.removeIf(u -> u.getName().equals(item.getName()));
        saveToFile();
    }
}