package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Student;

public interface StudentDAO extends DAO<Student> {

    Student getStudent(String username) throws UserNotFoundException, DAOException;
}
