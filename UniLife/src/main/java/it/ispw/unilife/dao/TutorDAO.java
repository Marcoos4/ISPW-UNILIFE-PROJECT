package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Tutor;

public interface TutorDAO extends DAO<Tutor> {

    Tutor getTutor(String username) throws UserNotFoundException, DAOException;
}
