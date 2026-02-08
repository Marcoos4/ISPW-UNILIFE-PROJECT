package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.LessonNotificationDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.enums.NotificationStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Lesson;
import it.ispw.unilife.model.Tutor;
import it.ispw.unilife.model.notification.LessonNotification;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBLessonNotificationDAO implements LessonNotificationDAO {
    private static final Logger logger = Logger.getLogger(DBLessonNotificationDAO.class.getName());
    private static DBLessonNotificationDAO instance = null;
    private final List<LessonNotification> cache = new ArrayList<>();

    private DBLessonNotificationDAO() throws DAOException {
        getAll();
    }

    public static DBLessonNotificationDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new DBLessonNotificationDAO();
        }
        return instance;
    }

    @Override
    public List<LessonNotification> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        // Mappatura esatta delle colonne visibili nello screenshot
        String query = "SELECT `username`, `timestamp`, `status`, `message`, `start`, `tutor_username` FROM `lesson_notification`";

        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // 1. Recupera il Tutor (che è anche il Sender della notifica)
                String tutorUsername = rs.getString("tutor_username");
                Tutor tutor = DBTutorDAO.getInstance().getTutor(tutorUsername);

                // 2. Recupera i dati della notifica
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                NotificationStatus status = NotificationStatus.fromString(rs.getString("status"));
                String message = rs.getString("message");

                // 3. Recupera la Lezione
                // Usiamo il tutor e l'orario di inizio ("start") per identificare la lezione univocamente
                LocalDateTime startTime = rs.getTimestamp("start").toLocalDateTime();

                // Nota: Assumiamo che DBLessonDAO abbia un metodo per recuperare la lezione tramite tutor e orario.
                // Se non esiste, dovrai implementarlo o recuperare la lista e filtrare.
                Lesson lesson = DBLessonDAO.getInstance().getLesson(tutorUsername, startTime);

                // 4. Crea l'oggetto notifica
                if (lesson != null) {
                    LessonNotification lessonNotification = new LessonNotification(
                            message, tutor, lesson, timestamp, status);
                    cache.add(lessonNotification);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error reading lesson notifications", e);
            throw new DAOException("Can't load lesson notifications");
        } catch (UserNotFoundException e) {
            throw new DAOException("User not found while loading notifications");
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(LessonNotification item) throws DAOException {
        // Query aggiornata con le colonne dello screenshot
        String query = "INSERT IGNORE INTO lesson_notification" +
                "(username, timestamp, status, message, start, tutor_username) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            // 1. username (Destinatario)
            stmt.setString(1, item.getSender().getUsername());

            // 2. timestamp
            stmt.setTimestamp(2, Timestamp.valueOf(item.getTimestamp()));

            // 3. status
            stmt.setString(3, item.getStatus().toString());

            // 4. message
            stmt.setString(4, item.getMessage());

            // 5. start (Data inizio lezione)
            stmt.setTimestamp(5, Timestamp.valueOf(item.getLesson().getStartTime()));

            // 6. tutor_username (Tutor della lezione / Mittente)
            stmt.setString(6, item.getLesson().getTutor().getUsername());

            stmt.executeUpdate();

            // Aggiungi alla cache
            cache.add(item);

        } catch (SQLException e) {
            throw new DAOException("Can't insert lesson notification: " + e.getMessage());
        }
    }

    @Override
    public void update(LessonNotification item) throws DAOException {
        String query = "UPDATE `lesson_notification` SET `status`=? WHERE `username`=? AND `timestamp`=?";

        Connection conn = ConnectionFactory.getConnection();

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
    public LessonNotification getLessonNotification(String username, LocalDateTime timestamp) throws DAOException {
        getAll();

        for (LessonNotification item : cache) {
            // Verifica corrispondenza: Sender (Tutor) e Timestamp
            if (item.getSender().getUsername().equals(username) &&
                    item.getTimestamp().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)
                            .equals(timestamp.truncatedTo(java.time.temporal.ChronoUnit.SECONDS))) {
                return item;
            }
        }

        // Se non in cache, prova query diretta (opzionale ma consigliato)
        return null;
    }

    @Override
    public void delete(LessonNotification item) throws DAOException {
        // Implementare cancellazione
    }
}