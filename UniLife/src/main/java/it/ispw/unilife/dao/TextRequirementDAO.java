package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.admission.TextRequirement;

import java.util.List;

public interface TextRequirementDAO extends DAO<TextRequirement> {
    public List<TextRequirement> getTextRequirements(String courseTitle, String universityName) throws UserNotFoundException, DAOException;
}
