package it.ispw.unilife.dao;

import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.exception.DAOException;

import java.util.List;

public interface CourseTagDAO extends DAO<CourseTags> {
    public List<CourseTags> getCourseTags(String courseName, String universityName) throws DAOException;
}
