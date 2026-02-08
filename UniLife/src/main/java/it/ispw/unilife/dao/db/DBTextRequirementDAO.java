package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.TextRequirementDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.admission.TextRequirement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBTextRequirementDAO implements TextRequirementDAO {

    private static final Logger logger = Logger.getLogger(DBTextRequirementDAO.class.getName());
    private static DBTextRequirementDAO instance = null;

    private DBTextRequirementDAO() {
    }

    public static DBTextRequirementDAO getInstance() {
        if (instance == null) {
            instance = new DBTextRequirementDAO();
        }
        return instance;
    }

    @Override
    public List<TextRequirement> getAll() throws DAOException {
        throw new UnsupportedOperationException("Not supported yet. Use getTextRequirements(courseTitle, uniName).");
    }

    @Override
    public void insert(TextRequirement item) throws DAOException {
        throw new DAOException("Use insert(item, course) instead");
    }

    @Override
    public void update(TextRequirement item) throws DAOException {
        throw new DAOException("Use update(item, course) instead");
    }

    @Override
    public void delete(TextRequirement item) throws DAOException {
        throw new DAOException("Use delete(item, course) instead");
    }

    public void insert(TextRequirement item, Course course) throws DAOException {
        String query = "INSERT INTO `text_requirement`" +
                "(`course_title`, `university_name`, `name`, `min_char`, `max_char`, `label`, `description`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, course.getCourseTitle());
            stmt.setString(2, course.getUniversity().getName());
            stmt.setString(3, item.getName());
            stmt.setInt(4, item.getMinChars());
            stmt.setInt(5, item.getMaxChars());
            stmt.setString(6, item.getLabel());
            stmt.setString(7, item.getDescription());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error inserting text requirement", e);
            throw new DAOException("Can't insert text requirement");
        }
    }

    public void update(TextRequirement item, Course course) throws DAOException {
        String query = "UPDATE `text_requirement` SET " +
                "`min_char`=?, `max_char`=?, `label`=?, `description`=? " +
                "WHERE `course_title`=? AND `university_name`=? AND `name`=?";

        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, item.getMinChars());
            stmt.setInt(2, item.getMaxChars());
            stmt.setString(3, item.getLabel());
            stmt.setString(4, item.getDescription());

            stmt.setString(5, course.getCourseTitle());
            stmt.setString(6, course.getUniversity().getName());
            stmt.setString(7, item.getName());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error updating text requirement", e);
            throw new DAOException("Can't update text requirement");
        }
    }

    public void delete(TextRequirement item, Course course) throws DAOException {
        String query = "DELETE FROM `text_requirement` WHERE `course_title`=? AND `university_name`=? AND `name`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, course.getCourseTitle());
            stmt.setString(2, course.getUniversity().getName());
            stmt.setString(3, item.getName());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error deleting text requirement", e);
            throw new DAOException("Can't delete text requirement");
        }
    }

    @Override
    public List<TextRequirement> getTextRequirements(String courseTitle, String universityName) throws DAOException {
        List<TextRequirement> results = new ArrayList<>();

        String query = "SELECT `name`, `label`, `description`, `min_char`, `max_char` " +
                "FROM `text_requirement` WHERE `course_title`=? AND `university_name`=?";

        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseTitle);
            stmt.setString(2, universityName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getString("name");
                    String label = rs.getString("label");
                    String desc = rs.getString("description");
                    int minChars = rs.getInt("min_char");
                    int maxChars = rs.getInt("max_char");

                    TextRequirement req = new TextRequirement(key, label, desc, minChars, maxChars);

                    results.add(req);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error fetching text requirements by course", e);
            throw new DAOException("Can't load text requirements");
        }
        return results;
    }
}