package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.ApplicationNotificationDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.enums.NotificationStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.User;
import it.ispw.unilife.model.admission.Application;
import it.ispw.unilife.model.notification.ApplicationNotification;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBApplicationNotificationDAO implements ApplicationNotificationDAO {
    private static final Logger logger = Logger.getLogger(DBApplicationNotificationDAO.class.getName());
    private static DBApplicationNotificationDAO instance = null;
    private static List<ApplicationNotification> cache = new ArrayList<>();

    private DBApplicationNotificationDAO() throws DAOException {
        try{
            getAll();
        } catch (DAOException | UserNotFoundException e) {
            throw new DAOException("User non trovato");
        }
    }

    public static DBApplicationNotificationDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new DBApplicationNotificationDAO();
        }
        return instance;
    }

    @Override
    public List<ApplicationNotification> getAll() throws DAOException, UserNotFoundException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        String query = "SELECT `username`, `timestamp`, `status`, `message`, `course_title`, `university_name`, `student_username`, `creation_date` FROM `application_notification`";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = DBUserDAO.getInstance().getUser(rs.getString("username"));
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                NotificationStatus status = NotificationStatus.fromString(rs.getString("status"));
                Application application = DBApplicationDAO.getInstance().getApplication(rs.getString("course_title"), rs.getString("university_name"), rs.getString("student_username"), rs.getTimestamp("creation_date").toLocalDateTime());

                // Usa il costruttore con timestamp e status dal DB
                ApplicationNotification applicationNotification = new ApplicationNotification(
                        rs.getString("message"), user, application, timestamp, status);

                cache.add(applicationNotification);
            }
        } catch (SQLException | UserNotFoundException e) {
            logger.log(Level.SEVERE, "DB Error reading reservation notifications", e);
            throw new DAOException("Can't load reservation notifications");
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(ApplicationNotification item) throws DAOException {
        String query = "INSERT IGNORE INTO application_notification" +
                "(username, timestamp, status, message, course_title, university_name, student_username, creation_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // MODIFICA: Connessione presa fuori dal try-with-resources
        Connection conn = ConnectionFactory.getConnection();

        // Nel try mettiamo solo il PreparedStatement così viene chiuso solo quello
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getSender().getUsername());
            stmt.setTimestamp(2, Timestamp.valueOf(item.getTimestamp()));
            stmt.setString(3, item.getStatus().toString());
            stmt.setString(4, item.getMessage());
            stmt.setString(5, item.getApplication().getCourse().getCourseTitle());
            stmt.setString(6, item.getApplication().getCourse().getUniversity().getName());
            stmt.setString(7, item.getApplication().getApplicant().getUsername());
            stmt.setTimestamp(8, Timestamp.valueOf(item.getApplication().getCreationDate()));

            stmt.executeUpdate();
            cache.add(item);

        } catch (SQLException e) {
            throw new DAOException("Can't insert application notification: " + e.getMessage());
        }
    }


    @Override
    public void update(ApplicationNotification item) throws DAOException {
        String query = "UPDATE `application_notification` SET `status`=? WHERE `username`=? AND `timestamp`=?";

        // MODIFICA: Connessione presa fuori dal try-with-resources
        Connection conn = ConnectionFactory.getConnection();

        // Nel try mettiamo solo il PreparedStatement così viene chiuso solo quello
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, NotificationStatus.COMPLETED.toString());
            stmt.setString(2, item.getSender().getUsername());
            stmt.setTimestamp(3, Timestamp.valueOf(item.getTimestamp()));

            stmt.executeUpdate();
            cache.add(item);

        } catch (SQLException e) {
            throw new DAOException("Can't insert application notification: " + e.getMessage());
        }
    }

    @Override
    public ApplicationNotification getApplicationNotification(String username, LocalDateTime timestamp) throws DAOException, UserNotFoundException {
        getAll();
        for (ApplicationNotification item : cache) {
            if (item.getSender().getUsername().equals(username) && item.getTimestamp().equals(timestamp)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void delete(ApplicationNotification item) throws DAOException {
        throw new DAOException("Devi specificare un destinatario!");
    }

}