package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.User;


public interface UserDAO extends DAO<User> {
    public User getUser(String username) throws UserNotFoundException, DAOException;
}
