package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.ReservationNotificationDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.enums.NotificationStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.*;
import it.ispw.unilife.model.notification.ReservationNotification;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBReservationNotificationDAO implements ReservationNotificationDAO {
    private static final Logger logger = Logger.getLogger(DBReservationNotificationDAO.class.getName());
    private static DBReservationNotificationDAO instance = null;
    private final List<ReservationNotification> cache = new ArrayList<>();

    private DBReservationNotificationDAO() throws DAOException {
        try{
            getAll();
        }catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static DBReservationNotificationDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new DBReservationNotificationDAO();
        }
        return instance;
    }

    @Override
    public List<ReservationNotification> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        String query = "SELECT `username`, `timestamp`, `status`, `message`, `student_username`, `tutor_username`, `start` FROM `reservation_notification` ";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = DBUserDAO.getInstance().getUser(rs.getString("username"));
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                NotificationStatus status = NotificationStatus.fromString(rs.getString("status"));
                Reservation reservation = DBReservationDAO.getInstance().getReservation(rs.getString("student_username"), rs.getString("tutor_username"), rs.getTimestamp("start").toLocalDateTime());

                // Usa il costruttore con timestamp e status dal DB
                ReservationNotification reservationNotification = new ReservationNotification(
                        rs.getString("message"), reservation, user, timestamp, status);

                cache.add(reservationNotification);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error reading reservation notifications", e);
            throw new DAOException("Can't load reservation notifications");
        } catch (UserNotFoundException e) {
            throw new DAOException(e.getMessage());
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(ReservationNotification item) throws DAOException {
        String query = "INSERT INTO `reservation_notification`(`username`, `timestamp`, `status`, `message`, `student_username`, `tutor_username`, `start`) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getSender().getUsername());
            stmt.setTimestamp(2, Timestamp.valueOf(item.getTimestamp()));
            stmt.setString(3, item.getStatus().toString());
            stmt.setString(4, item.getMessage());
            stmt.setString(5, item.getReservation().checkStudent().getUsername());
            stmt.setString(6, item.getReservation().checkTutor().getUsername());
            stmt.setTimestamp(7, Timestamp.valueOf(item.getReservation().checkLesson().getStartTime()));

            stmt.executeUpdate();

            cache.add(item);
            logger.info("Reservation notification salvata correttamente nel database!");

        } catch (SQLException e) {
            throw new DAOException("Can't insert reservation notification: " + e.getMessage());
        }

    }

    @Override
    public void update(ReservationNotification item) throws DAOException {
        String query = "UPDATE `reservation_notification` SET `status` = ? " +
                "WHERE `username` = ? AND `timestamp` = ? AND `student_username` = ? AND `tutor_username` = ? AND `start` = ?";

        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getStatus().toString());
            stmt.setString(2, item.getSender().getUsername());
            stmt.setTimestamp(3, Timestamp.valueOf(item.getTimestamp()));
            stmt.setString(4, item.getReservation().checkStudent().getUsername());
            stmt.setString(5, item.getReservation().checkTutor().getUsername());
            stmt.setTimestamp(6, Timestamp.valueOf(item.getReservation().checkLesson().getStartTime()));

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                logger.info("Stato notifica aggiornato correttamente nel database!");
            } else {
                logger.warning("Nessuna notifica trovata da aggiornare per questa prenotazione.");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in update notification", e);
            throw new DAOException("Can't update notification status");
        }
    }

    @Override
    public void delete(ReservationNotification item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReservationNotification getReservationNotification(String senderUsername, LocalDateTime date) throws DAOException {
        getAll();
        for (ReservationNotification item : cache) {
            if (item.getSender().getUsername().equals(senderUsername) && item.getTimestamp().equals(date)) {
                return item;
            }
        }
        return null;
    }
}