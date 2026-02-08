package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.ApplicationDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.admission.Application;
import it.ispw.unilife.enums.ApplicationStatus;
import it.ispw.unilife.model.admission.ApplicationItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBApplicationDAO implements ApplicationDAO {

    private static final Logger logger = Logger.getLogger(DBApplicationDAO.class.getName());
    private static DBApplicationDAO instance = null;
    private final List<Application> cache = new ArrayList<>();

    private DBApplicationDAO() {
        try{
            getAll();
        }catch (DAOException e) {
            logger.severe(e.getMessage());
        }
    }

    public static DBApplicationDAO getInstance() {
        if (instance == null) {
            instance = new DBApplicationDAO();
        }
        return instance;
    }

    @Override
    public List<Application> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        String query = "SELECT `course_title`, `university_name`, `creation_date`, `student_username`, `submission_date`, `status` FROM `application`";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String courseTitle = rs.getString("course_title");
                String universityName = rs.getString("university_name");
                LocalDateTime creationDate = rs.getTimestamp("creation_date").toLocalDateTime();
                String studentUsername = rs.getString("student_username");

                LocalDateTime submissionDate = rs.getTimestamp("submission_date").toLocalDateTime();

                String statusStr = rs.getString("status");



                List<ApplicationItem> items = DBApplicationItemDAO.getInstance().getItems(courseTitle, universityName, studentUsername, creationDate);

                Application app = new Application(ApplicationStatus.fromString(statusStr), submissionDate, creationDate, DBStudentDAO.getInstance().getStudent(studentUsername), DBCourseDAO.getInstance().getCourse(courseTitle, universityName), items);
                cache.add(app);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error reading applications", e);
            throw new DAOException("Can't load applications");
        }catch (UserNotFoundException e){
            logger.log(Level.SEVERE, "User not found", e);
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Application item) throws DAOException {
        String query = "INSERT INTO `application`(`course_title`, `university_name`, `creation_date`, `student_username`, `submission_date`, `status`) VALUES (?,?,?,?,?,?)";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getCourse().getCourseTitle());
            stmt.setString(2, item.getCourse().getUniversity().getName());
            stmt.setTimestamp(3, Timestamp.valueOf(item.getCreationDate()));
            stmt.setString(4, item.getApplicant().getUsername());

            if (item.getSubmissionDate() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(item.getSubmissionDate()));
            } else {
                stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now())); // o stmt.setNull(...)
            }

            stmt.setString(6, item.getStatus().toString());
            stmt.executeUpdate();

            for(ApplicationItem applicationItem : item.getItems()) {
                DBApplicationItemDAO.getInstance().insert(applicationItem, item);
            }

            cache.add(item);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in insert application", e);
            throw new DAOException("Can't insert application");
        }
    }

    @Override
    public void update(Application item) throws DAOException {
        // Aggiorniamo status e data sottomissione basandoci sulla chiave composta (4 campi)
        String query = "UPDATE `application` SET `status`=?, `submission_date`=? WHERE `course_title`=? AND `university_name`=? AND `student_username`=? AND `creation_date`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            // Parametri da aggiornare
            stmt.setString(1, item.getStatus().toString());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now())); // Aggiorna all'ora corrente o item.getSubmissionDate()

            // Parametri della WHERE (Chiave Primaria)
            stmt.setString(3, item.getCourse().getCourseTitle());
            stmt.setString(4, item.getCourse().getUniversity().getName());
            stmt.setString(5, item.getApplicant().getUsername());
            stmt.setTimestamp(6, Timestamp.valueOf(item.getCreationDate()));

            stmt.executeUpdate();
            cache.removeIf(a -> a.equals(item));
            cache.add(item);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error updating application", e);
            throw new DAOException("Can't update application");
        }
    }

    @Override
    public void delete(Application item) throws DAOException {
        String query = "DELETE FROM `application` WHERE `course_title`=? AND `university_name`=? AND `student_username`=? AND `creation_date`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getCourse().getCourseTitle());
            stmt.setString(2, item.getCourse().getUniversity().getName());
            stmt.setString(3, item.getApplicant().getUsername());
            stmt.setTimestamp(4, Timestamp.valueOf(item.getCreationDate()));

            stmt.executeUpdate();

            cache.removeIf(a-> a.equals(item));

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error deleting application", e);
            throw new DAOException("Can't delete application");
        }
    }

    @Override
    public List<Application> getApplications(String username) throws DAOException {
        getAll();
        List<Application> res = new ArrayList<>();
        for (Application application : cache){
            if (application.getApplicant().getUsername().equals(username)) {
                res.add(application);
            }
        }
        return res;
    }

    @Override
    public Application getApplication(String courseTitle, String universityName, String studentUsername, LocalDateTime creationDate) throws DAOException {
        getAll();
        for (Application application : cache){
            if (application.getApplicant().getUsername().equals(studentUsername) && application.getCourse().getCourseTitle().equals(courseTitle)
            && application.getCourse().getUniversity().getName().equals(universityName) && application.getSubmissionDate().equals(creationDate)) {
                return application;
            }
        }
        return null;
    }

}