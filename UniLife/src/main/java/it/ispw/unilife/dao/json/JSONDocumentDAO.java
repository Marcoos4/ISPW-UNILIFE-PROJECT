package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.DocumentDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Document;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class JSONDocumentDAO implements DocumentDAO {

    private static final String FILE_NAME = "documents.json";
    private static JSONDocumentDAO instance = null;
    private final List<Document> cache = new ArrayList<>();

    private JSONDocumentDAO() throws DAOException {
        try {
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static synchronized JSONDocumentDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new JSONDocumentDAO();
        }
        return instance;
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        List<JsonRecords.DocumentRecord> records = new ArrayList<>();
        for (Document d : cache) {
            JsonRecords.DocumentRecord r = new JsonRecords.DocumentRecord();
            r.setName(d.getFileName());
            r.setType(d.getFileType());
            r.setSize(d.getFileSize());
            r.setContentBase64(d.getContent() != null ? Base64.getEncoder().encodeToString(d.getContent()) : null);
            records.add(r);
        }
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), gson.toJson(records));
    }

    @Override
    public Document getDocument(String documentName) throws DAOException {
        if (cache.isEmpty()) {
            getAll();
        }
        for (Document document : cache) {
            if (document.getFileName().equals(documentName)) {
                return document;
            }
        }
        return null;
    }

    @Override
    public List<Document> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        String json = JsonUtil.readFile(file);

        Type listType = new TypeToken<List<JsonRecords.DocumentRecord>>() {}.getType();
        List<JsonRecords.DocumentRecord> records = gson.fromJson(json, listType);

        if (records != null) {
            for (JsonRecords.DocumentRecord r : records) {
                byte[] content = r.getContentBase64() != null ? Base64.getDecoder().decode(r.getContentBase64()) : null;
                Document document = new Document(r.getName(), r.getType(), r.getSize(), content);
                cache.add(document);
            }
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Document item) throws DAOException {
        cache.add(item);
        saveToFile();
    }

    @Override
    public void update(Document item) throws DAOException {
        cache.removeIf(d -> d.getFileName().equals(item.getFileName()));
        cache.add(item);
        saveToFile();
    }

    @Override
    public void delete(Document item) throws DAOException {
        cache.removeIf(d -> d.getFileName().equals(item.getFileName()));
        saveToFile();
    }
}