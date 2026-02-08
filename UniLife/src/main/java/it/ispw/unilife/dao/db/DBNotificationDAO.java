package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.NotificationDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.User;
import it.ispw.unilife.model.notification.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBNotificationDAO implements NotificationDAO {
    private static final Logger logger = Logger.getLogger(DBNotificationDAO.class.getName());
    private static DBNotificationDAO instance = null;

    private DBNotificationDAO() {
    }

    public static DBNotificationDAO getInstance() {
        if (instance == null) {
            instance = new DBNotificationDAO();
        }
        return instance;
    }

    @Override
    public List<Notification> getAll(User user) throws DAOException {
        String query = "SELECT `sender_username`, `timestamp` FROM `user_notification` WHERE `username` = ? ORDER BY `timestamp` DESC";
        List<Notification> notifications = new ArrayList<>();

        // MODIFICA: Connessione fuori
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)){
             stmt.setString(1, user.getUsername());
             ResultSet rs = stmt.executeQuery();  // Anche il ResultSet si chiude col try, ma la connessione resta viva

            while (rs.next()) {
                String senderUsername = rs.getString("sender_username");
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();

                Notification notification = null;

                notification = DBReservationNotificationDAO.getInstance().getReservationNotification(senderUsername, timestamp);

                // 2. Prova Application
                if (notification == null) {
                    notification = DBApplicationNotificationDAO.getInstance().getApplicationNotification(senderUsername, timestamp);
                }

                // 3. Prova Lesson
                if (notification == null) {
                    notification = DBLessonNotificationDAO.getInstance().getLessonNotification(senderUsername, timestamp);
                }

                if (notification != null) {
                    notifications.add(notification);
                }
            }
            return notifications;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error reading notifications", e);
            throw new DAOException("Can't load notifications");
        } catch (UserNotFoundException e) {
            throw new DAOException("User not found");
        }
    }

    @Override
    public List<Notification> getAll() throws DAOException {
        throw new UnsupportedOperationException("Not supported. Use getAll(User user).");
    }

    @Override
    public void insert(Notification item, User user) throws DAOException {
        String query = "INSERT IGNORE INTO user_notification(username, sender_username, timestamp) VALUES (?, ?, ?)";

        // MODIFICA: Connessione fuori
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, item.getSender().getUsername());
            stmt.setTimestamp(3, Timestamp.valueOf(item.getTimestamp()));

            stmt.executeUpdate();

            // Delega l'inserimento dei dettagli
            if (item instanceof ReservationNotification reservationNotification) {
                DBReservationNotificationDAO.getInstance().insert(reservationNotification);

            } else if (item instanceof LessonNotification lessonNotification) {
                DBLessonNotificationDAO.getInstance().insert(lessonNotification);

            } else if (item instanceof ApplicationNotification applicationNotification) {
                // Passiamo 'user' per la tabella specifica
                DBApplicationNotificationDAO.getInstance().insert(applicationNotification);
            }

            logger.info("Notifica salvata correttamente per: " + user.getUsername());

        } catch (SQLException e) {
            throw new DAOException("Can't insert notification link: " + e.getMessage());
        }
    }

    @Override
    public void insert(Notification notification) throws DAOException {
        throw new UnsupportedOperationException("Use insert(Notification item, User recipient) instead.");
    }

    @Override
    public void update(Notification item) throws DAOException {
        if (item instanceof ReservationNotification reservationNotification) {
            DBReservationNotificationDAO.getInstance().update(reservationNotification);
        } else if (item instanceof LessonNotification  lessonNotification) {
            DBLessonNotificationDAO.getInstance().update(lessonNotification);
        } else if (item instanceof ApplicationNotification applicationNotification) {
            DBApplicationNotificationDAO.getInstance().update(applicationNotification);
        }
    }

    @Override
    public void delete(Notification item) throws DAOException {
        Logger.getLogger("DBNotificationDAO").info("Metodo da implementare");
    }
}