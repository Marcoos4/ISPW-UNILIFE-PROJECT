package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.LessonDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Lesson;
import it.ispw.unilife.enums.LessonStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBLessonDAO implements LessonDAO {

    private static final Logger logger = Logger.getLogger(DBLessonDAO.class.getName());
    private static DBLessonDAO instance = null;
    private final List<Lesson> cache = new ArrayList<>();

    private DBLessonDAO() throws DAOException {
        try{
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }

    }

    public static DBLessonDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new DBLessonDAO();
        }
        return instance;
    }

    @Override
    public List<Lesson> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        String query = "SELECT `subject`, `price`, `start`, `end`, `duration`, `tutor_username`, `status` FROM `lesson`";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String subject = rs.getString("subject");
                LocalDateTime start = rs.getTimestamp("start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("end").toLocalDateTime();
                float price = rs.getFloat("price");
                String statusStr = rs.getString("status");
                String tutorUsername = rs.getString("tutor_username");

                Lesson l = new Lesson(subject, start, end, price, DBTutorDAO.getInstance().getTutor(tutorUsername), LessonStatus.fromString(statusStr));
                cache.add(l);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error reading lessons", e);
            throw new DAOException("Can't load lessons");
        } catch (UserNotFoundException e) {
            throw new DAOException("Can't find user");
        }
        return new ArrayList<>(cache);
    }



    @Override
    public void insert(Lesson item) throws DAOException {
        String query = "INSERT INTO `lesson`(`subject`, `price`, `start`, `end`, `duration`, `tutor_username`, `status`) VALUES (?,?,?,?,?,?,?)";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getSubject());
            stmt.setTimestamp(3, Timestamp.valueOf(item.getStartTime().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)));
            stmt.setTimestamp(4, Timestamp.valueOf(item.getEndTime().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)));
            stmt.setFloat(2, item.getPricePerHour());
            stmt.setInt(5, item.getDurationInHours());
            stmt.setString(6, item.getTutor().getUsername());
            stmt.setString(7, item.getStatus().toString());

            stmt.executeUpdate();
            cache.add(item);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in insert lesson", e);
            throw new DAOException("Can't insert lesson");
        }
    }

    @Override
    public void update(Lesson item) throws DAOException {
        String query = "UPDATE `lesson` SET `status`=? WHERE `tutor_username`=? AND `start`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getStatus().toString());
            stmt.setString(2, item.getTutor().getUsername());
            stmt.setTimestamp(3, Timestamp.valueOf(item.getStartTime()));

            stmt.executeUpdate();
            cache.removeIf(l ->
                    l.getTutor().getUsername().equals(item.getTutor().getUsername()) &&
                            l.getStartTime().equals(item.getStartTime()));
            cache.add(item);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error updating lesson", e);
            throw new DAOException("Can't update lesson");
        }
    }

    @Override
    public void delete(Lesson item) throws DAOException {
        String query = "DELETE FROM `lesson` WHERE `tutor_username`=? AND `startTime`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getTutor().getUsername());
            stmt.setTimestamp(2, Timestamp.valueOf(item.getStartTime()));

            stmt.executeUpdate();
            cache.removeIf(l ->
                    l.getTutor().getUsername().equals(item.getTutor().getUsername()) &&
                            l.getStartTime().equals(item.getStartTime()));

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error deleting lesson", e);
            throw new DAOException("Can't delete lesson");
        }
    }

    @Override
    public Lesson getLesson(String username, LocalDateTime creationDate) throws DAOException {
        getAll();
        LocalDateTime searchDate = creationDate.truncatedTo(java.time.temporal.ChronoUnit.SECONDS);

        for (Lesson lesson : cache){
            LocalDateTime lessonDate = lesson.getStartTime().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);

            if (lesson.getTutor().getUsername().equals(username) && lessonDate.equals(searchDate)) {
                return lesson;
            }
        }
        return null;
    }
}