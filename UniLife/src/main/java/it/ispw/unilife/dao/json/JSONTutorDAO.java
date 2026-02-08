package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.TutorDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Tutor;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONTutorDAO implements TutorDAO {

    private static final String FILE_NAME = "tutors.json";
    private static JSONTutorDAO instance = null;
    private final List<Tutor> cache = new ArrayList<>();

    private JSONTutorDAO() throws DAOException {
        try {
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static synchronized JSONTutorDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new JSONTutorDAO();
        }
        return instance;
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        List<JsonRecords.TutorRecord> records = new ArrayList<>();
        for (Tutor t : cache) {
            JsonRecords.TutorRecord r = new JsonRecords.TutorRecord();
            r.setUsername(t.getUsername());
            r.setName(t.getName());
            r.setSurname(t.getSurname());
            r.setPassword(t.getPassword());
            r.setRating(t.getRating());
            records.add(r);
        }
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), gson.toJson(records));
    }

    @Override
    public Tutor getTutor(String username) throws UserNotFoundException, DAOException {
        if (cache.isEmpty()) {
            getAll();
        }
        for (Tutor tutor : cache) {
            if (tutor.getUsername().equalsIgnoreCase(username)) {
                return tutor;
            }
        }
        return null;
    }

    @Override
    public List<Tutor> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        String json = JsonUtil.readFile(file);

        Type listType = new TypeToken<List<JsonRecords.TutorRecord>>() {}.getType();
        List<JsonRecords.TutorRecord> records = gson.fromJson(json, listType);

        if (records != null) {
            for (JsonRecords.TutorRecord r : records) {
                Tutor tutor = new Tutor(r.getUsername(), r.getName(), r.getSurname(), r.getPassword());
                tutor.updateRating(r.getRating());
                cache.add(tutor);
            }
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Tutor item) throws DAOException {
        cache.add(item);
        saveToFile();
    }

    @Override
    public void update(Tutor item) throws DAOException {
        cache.removeIf(t -> t.getUsername().equals(item.getUsername()));
        cache.add(item);
        saveToFile();
    }

    @Override
    public void delete(Tutor item) throws DAOException {
        cache.removeIf(t -> t.getUsername().equals(item.getUsername()));
        saveToFile();
    }
}