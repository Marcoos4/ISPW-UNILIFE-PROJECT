package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Document;

public interface DocumentDAO extends DAO<Document> {
    Document getDocument(String documentName) throws DAOException;
}
