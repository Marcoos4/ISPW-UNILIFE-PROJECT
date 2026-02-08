package it.ispw.unilife.dao;

import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.exception.DAOException;

import java.util.List;

public interface StudentInterestDAO extends DAO <CourseTags> {
    public List<CourseTags> getStudentInterests(String username) throws DAOException;
}
