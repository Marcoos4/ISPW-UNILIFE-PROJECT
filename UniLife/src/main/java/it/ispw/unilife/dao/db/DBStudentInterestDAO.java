package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.StudentInterestDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBStudentInterestDAO implements StudentInterestDAO {

    private static final Logger logger = Logger.getLogger(DBStudentInterestDAO.class.getName());
    private static DBStudentInterestDAO instance = null;

    private DBStudentInterestDAO() {
    }

    public static DBStudentInterestDAO getInstance() {
        if (instance == null) {
            instance = new DBStudentInterestDAO();
        }
        return instance;
    }

    @Override
    public List<CourseTags> getStudentInterests(String studentUsername) throws DAOException {
        List<CourseTags> results = new ArrayList<>();

        // Basato su image_81274c.png: colonne student_username e tag
        String query = "SELECT `tag` FROM `student_interest` WHERE `student_username`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentUsername);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CourseTags tag = CourseTags.fromString(rs.getString("tag"));
                    if (tag != null) {
                        results.add(tag);
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error fetching student interests", e);
            throw new DAOException("Can't load student interests");
        }
        return results;
    }

    // --- METODI SPECIFICI CON PASSAGGIO DELLO STUDENTE (COMPOSIZIONE) ---

    public void insert(CourseTags item, Student student) throws DAOException {
        String query = "INSERT INTO `student_interest`(`student_username`, `tag`) VALUES (?, ?)";

        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, student.getUsername());
            stmt.setString(2, item.toString());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error inserting student interest", e);
            throw new DAOException("Can't insert student interest");
        }
    }

    public void update(CourseTags item, Student student) throws DAOException {
        // Logica Delete + Insert come nel tuo esempio
        delete(item, student);
        insert(item, student);
    }

    public void delete(CourseTags item, Student student) throws DAOException {
        String query = "DELETE FROM `student_interest` WHERE `student_username`=? AND `tag`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, student.getUsername());
            stmt.setString(2, item.toString());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error deleting student interest", e);
            throw new DAOException("Can't delete student interest");
        }
    }

    // --- METODI STANDARD DELL'INTERFACCIA (Stub) ---

    @Override
    public List<CourseTags> getAll() throws DAOException {
        return List.of();
    }

    @Override
    public void insert(CourseTags item) throws DAOException {
        // Non supportato senza lo studente padre
    }

    @Override
    public void update(CourseTags item) throws DAOException {
        // Non supportato senza lo studente padre
    }

    @Override
    public void delete(CourseTags item) throws DAOException {
        // Non supportato senza lo studente padre
    }
}