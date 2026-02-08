package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.DocumentDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Document;

import java.util.ArrayList;
import java.util.List;

public class DemoDocumentDAO implements DocumentDAO {

    private static DemoDocumentDAO instance = null;
    private final List<Document> cache = new ArrayList<>();

    private DemoDocumentDAO() {}

    public static DemoDocumentDAO getInstance() {
        if (instance == null) {
            instance = new DemoDocumentDAO();
        }
        return instance;
    }

    @Override
    public Document getDocument(String documentName) throws DAOException {
        for (Document document : cache) {
            if (document.getFileName().equals(documentName)) {
                return document;
            }
        }
        return null;
    }

    @Override
    public List<Document> getAll() throws DAOException {
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Document item) throws DAOException {
        cache.add(item);
    }

    @Override
    public void update(Document item) throws DAOException {
        cache.removeIf(d -> d.getFileName().equals(item.getFileName()));
        cache.add(item);
    }

    @Override
    public void delete(Document item) throws DAOException {
        cache.removeIf(d -> d.getFileName().equals(item.getFileName()));
    }
}
