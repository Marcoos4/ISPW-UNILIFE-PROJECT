package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.CourseTagDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBCourseTagDAO implements CourseTagDAO {

    private static final Logger logger = Logger.getLogger(DBCourseTagDAO.class.getName());
    private static DBCourseTagDAO instance = null;

    private DBCourseTagDAO() {

    }

    public static DBCourseTagDAO getInstance() {
        if (instance == null) {
            instance = new DBCourseTagDAO();
        }
        return instance;
    }

    @Override
    public List<CourseTags> getCourseTags(String courseName, String universityName) throws DAOException {
        List<CourseTags> results = new ArrayList<>();

        String query = "SELECT `course_title`, `university_name`, `tag` FROM `course_tags` WHERE `course_title`=? AND `university_name`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseName);
            stmt.setString(2, universityName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CourseTags tag = CourseTags.fromString(rs.getString("tag"));

                    results.add(tag);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error fetching document requirements by course", e);
            throw new DAOException("Can't load document requirements");
        }
        return results;
    }

    public void insert(CourseTags item, Course course) throws DAOException {
        String query = "INSERT INTO `course_tags`" +
                " (`course_title`, `university_name`, `tag`)" +
                " VALUES (?, ?, ?)";

        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, course.getCourseTitle());
            stmt.setString(2, course.getUniversity().getName());
            stmt.setString(3, item.toString());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error inserting document requirement", e);
            throw new DAOException("Can't insert document requirement");
        }
    }

    public void update(CourseTags item, Course course) throws DAOException {

        delete(item, course);
        insert(item, course);

    }

    public void delete(CourseTags item, Course course) throws DAOException {
        String query = "DELETE FROM `course_tags` WHERE `course_title`=? AND `university_name`=? AND `tag`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, course.getCourseTitle());
            stmt.setString(2, course.getUniversity().getName());
            stmt.setString(3, item.toString());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error deleting document requirement", e);
            throw new DAOException("Can't delete document requirement");
        }
    }

    @Override
    public List<CourseTags> getAll() throws DAOException {
        return List.of();
    }

    @Override
    public void insert(CourseTags item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");

    }

    @Override
    public void update(CourseTags item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(CourseTags item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
