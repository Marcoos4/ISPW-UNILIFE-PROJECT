package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.admission.DocumentRequirement;

import java.util.List;

public interface DocumentRequirementDAO extends DAO<DocumentRequirement> {
    public List<DocumentRequirement> getDocumentRequirements(String courseTitle, String universityName) throws DAOException;
}
