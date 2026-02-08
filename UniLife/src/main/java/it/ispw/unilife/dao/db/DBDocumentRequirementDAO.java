package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.DocumentRequirementDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.admission.DocumentRequirement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBDocumentRequirementDAO implements DocumentRequirementDAO {

    private static final Logger logger = Logger.getLogger(DBDocumentRequirementDAO.class.getName());
    private static DBDocumentRequirementDAO instance = null;

    private DBDocumentRequirementDAO() {
    }

    public static DBDocumentRequirementDAO getInstance() {
        if (instance == null) {
            instance = new DBDocumentRequirementDAO();
        }
        return instance;
    }

    @Override
    public List<DocumentRequirement> getAll() throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insert(DocumentRequirement item) throws DAOException {
        throw new DAOException("Use insert(item, course) instead");
    }

    @Override
    public void update(DocumentRequirement item) throws DAOException {
        throw new DAOException("Use update(item, course) instead");
    }

    @Override
    public void delete(DocumentRequirement item) throws DAOException {
        throw new DAOException("Use delete(item, course) instead");
    }

    // --- METODI SPECIFICI CON PASSAGGIO DEL CORSO (COMPOSIZIONE) ---

    public void insert(DocumentRequirement item, Course course) throws DAOException {
        String query = "INSERT INTO `document_requirement`" +
                "(`course_title`, `university_name`, `name`, `max_size`, `allowed_extension`, `is_certificate`, `label`, `description`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, course.getCourseTitle());
            stmt.setString(2, course.getUniversity().getName());
            stmt.setString(3, item.getName());
            stmt.setDouble(4, item.getMaxSizeMB()); // Nota: ho usato il getter corretto dal tuo model (getMaxSizeMB)
            stmt.setString(5, item.getAllowedExtension());
            stmt.setBoolean(6, item.isCertificate());
            stmt.setString(7, item.getLabel());
            stmt.setString(8, item.getDescription());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error inserting document requirement", e);
            throw new DAOException("Can't insert document requirement");
        }
    }

    public void update(DocumentRequirement item, Course course) throws DAOException {
        String query = "UPDATE `document_requirement` SET " +
                "`max_size`=?, `allowed_extension`=?, `is_certificate`=?, `label`=?, `description`=? " +
                "WHERE `course_title`=? AND `university_name`=? AND `name`=?";

        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, item.getMaxSizeMB());
            stmt.setString(2, item.getAllowedExtension());
            stmt.setBoolean(3, item.isCertificate());
            stmt.setString(4, item.getLabel());
            stmt.setString(5, item.getDescription());

            stmt.setString(6, course.getCourseTitle());
            stmt.setString(7, course.getUniversity().getName());
            stmt.setString(8, item.getName());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error updating document requirement", e);
            throw new DAOException("Can't update document requirement");
        }
    }

    public void delete(DocumentRequirement item, Course course) throws DAOException {
        String query = "DELETE FROM `document_requirement` WHERE `course_title`=? AND `university_name`=? AND `name`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, course.getCourseTitle());
            stmt.setString(2, course.getUniversity().getName());
            stmt.setString(3, item.getName());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error deleting document requirement", e);
            throw new DAOException("Can't delete document requirement");
        }
    }

    @Override
    public List<DocumentRequirement> getDocumentRequirements(String courseTitle, String universityName) throws DAOException {
        List<DocumentRequirement> results = new ArrayList<>();

        String query = "SELECT `course_title`, `university_name`, `max_size`, `allowed_extension`, `is_certificate`, `name`, `label`, `description` FROM `document_requirement` WHERE `course_title`=? AND `university_name`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseTitle);
            stmt.setString(2, universityName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getString("name");
                    double maxSize = rs.getDouble("max_size");
                    String ext = rs.getString("allowed_extension");
                    boolean isCert = rs.getBoolean("is_certificate");
                    String label = rs.getString("label");
                    String desc = rs.getString("description");
                    // Costruttore basato sul tuo model:
                    // (key, label, description, mandatory, extension, maxSizeMB, isCertificate)
                    DocumentRequirement req = new DocumentRequirement(key, label, desc, ext, (long)maxSize, isCert);

                    results.add(req);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error fetching document requirements by course", e);
            throw new DAOException("Can't load document requirements");
        }
        return results;
    }

}