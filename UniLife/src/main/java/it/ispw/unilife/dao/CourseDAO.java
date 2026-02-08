package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;

public interface CourseDAO extends DAO<Course> {

    Course getCourse(String courseTitle, String universityName) throws DAOException;
}
