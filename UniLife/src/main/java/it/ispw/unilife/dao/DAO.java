package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;

import java.util.List;

public interface DAO<T> {

    List<T> getAll() throws DAOException, UserNotFoundException;

    void insert(T item) throws DAOException;

    void update(T item) throws DAOException;

    void delete(T item) throws DAOException;

}
