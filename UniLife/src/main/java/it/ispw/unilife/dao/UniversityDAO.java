package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.University;

public interface UniversityDAO extends DAO<University> {

     University getUniversity(String uniName) throws DAOException;
}
