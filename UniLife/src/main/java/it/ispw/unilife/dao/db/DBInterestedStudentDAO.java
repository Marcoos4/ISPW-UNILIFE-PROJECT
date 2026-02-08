package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.InterestedStudentDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBInterestedStudentDAO implements InterestedStudentDAO {

    private static final Logger logger = Logger.getLogger(DBInterestedStudentDAO.class.getName());
    private static DBInterestedStudentDAO instance = null;

    private DBInterestedStudentDAO() {
    }

    public static DBInterestedStudentDAO getInstance() {
        if (instance == null) {
            instance = new DBInterestedStudentDAO();
        }
        return instance;
    }

    @Override
    public List<Student> getInterestedStudents(String courseTitle, String universityName) throws DAOException {
        List<Student> results = new ArrayList<>();
        String query = "SELECT `username` FROM `interested_student` WHERE `course_name`=? AND `university_name`=?";

        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseTitle);
            stmt.setString(2, universityName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("username");

                    Student s = (Student) DBUserDAO.getInstance().getUser(username);

                    results.add(s);
                }
            }
        } catch (SQLException | UserNotFoundException e) {
            logger.log(Level.SEVERE, "DB Error fetching interested students", e);
            throw new DAOException("Can't load interested students");
        }
        return results;
    }

    @Override
    public List<Course> getInterestedCourse(String username) throws DAOException {
        List<Course> results = new ArrayList<>();
        String query = "SELECT `course_name`, `university_name` FROM `interested_student` WHERE `username`=?";

        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String courseName = rs.getString("course_name");
                    String universityName = rs.getString("university_name");

                    Course c = DBCourseDAO.getInstance().getCourse(courseName, universityName);

                    results.add(c);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error fetching interested students", e);
            throw new DAOException("Can't load interested students");
        }
        return results;
    }

    public void insert(Student item, Course course) throws DAOException {
        String query = "INSERT INTO `interested_student`" +
                "(`username`, `course_name`, `university_name`) " +
                "VALUES (?, ?, ?)";

        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getUsername());
            stmt.setString(2, course.getCourseTitle());
            stmt.setString(3, course.getUniversity().getName());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error inserting interested student", e);
            throw new DAOException("Can't insert interested student");
        }
    }

    public void update(Student item, Course course) throws DAOException {
        delete(item, course);
        insert(item, course);
    }

    public void delete(Student item, Course course) throws DAOException {
        String query = "DELETE FROM `interested_student` WHERE `username`=? AND `course_name`=? AND `university_name`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getUsername());
            stmt.setString(2, course.getCourseTitle());
            stmt.setString(3, course.getUniversity().getName());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error deleting interested student", e);
            throw new DAOException("Can't delete interested student");
        }
    }

    @Override
    public List<Student> getAll() throws DAOException {
        return List.of();
    }

    @Override
    public void insert(Student item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Student item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Student item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}