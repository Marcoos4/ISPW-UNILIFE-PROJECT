package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.Student;

import java.util.List;

public interface InterestedStudentDAO extends DAO<Student> {
    List<Student> getInterestedStudents(String courseTitle, String universityName) throws DAOException;
    List<Course> getInterestedCourse(String username) throws DAOException;
}
