package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.ReservationDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.*;
import it.ispw.unilife.enums.ReservationStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBReservationDAO implements ReservationDAO {

    private static final Logger logger = Logger.getLogger(DBReservationDAO.class.getName());
    private static DBReservationDAO instance = null;
    private final List<Reservation> cache = new ArrayList<>();

    private DBReservationDAO() throws DAOException {
        try{
            getAll();
        }catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static DBReservationDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new DBReservationDAO();
        }
        return instance;
    }

    @Override
    public List<Reservation> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        String query = "SELECT `student_username`, `tutor_username_lesson`, `start_date_time`, `status`, `payment_stripe` FROM `reservation`";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Student student = DBStudentDAO.getInstance().getStudent(rs.getString("student_username"));
                Lesson lesson = DBLessonDAO.getInstance().getLesson(rs.getString("tutor_username_lesson"), rs.getTimestamp("start_date_time").toLocalDateTime());
                Payment payment = DBPaymentDAO.getInstance().getPayment(rs.getString("payment_stripe"));
                Reservation reservation = new Reservation(student, lesson, payment, ReservationStatus.fromString(rs.getString("status")));
                cache.add(reservation);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error reading applications", e);
            throw new DAOException("Can't load applications");
        } catch (UserNotFoundException e) {
            throw new DAOException(e.getMessage());
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Reservation item) throws DAOException {
        String query = "INSERT INTO `reservation`(`student_username`, `tutor_username_lesson`, `start_date_time`, `status`, `payment_stripe`)" +
                " VALUES (?, ?, ?, ?, ?)";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.checkStudent().getUsername());
            stmt.setString(2, item.checkTutor().getUsername());
            stmt.setTimestamp(3, Timestamp.valueOf(item.checkLesson().getStartTime()));
            stmt.setString(4, item.getStatus().toString());

            if (item.checkPayment() != null && item.checkPayment().getStripe() != null) {
                DBPaymentDAO.getInstance().insert(item.checkPayment());
                stmt.setString(5, item.checkPayment().getStripe().getId());
                stmt.executeUpdate();
            } else {
                stmt.setNull(5, java.sql.Types.VARCHAR);
                stmt.executeUpdate();
            }
            cache.add(item);
        } catch (SQLException e) {
            throw new DAOException("Can't insert reservation: " + e.getMessage());
        }
    }

    @Override
    public void update(Reservation item) throws DAOException {
        if (item.checkPayment() != null && item.checkPayment().getStripe() != null) {
            String stripeId = item.checkPayment().getStripe().getId();

            Payment existingPayment = DBPaymentDAO.getInstance().getPayment(stripeId);

            if (existingPayment == null) {
                DBPaymentDAO.getInstance().insert(item.checkPayment());
            } else {
                DBPaymentDAO.getInstance().update(item.checkPayment());
            }
        }

        String query = "UPDATE `reservation` SET `status`=?, `payment_stripe`=? WHERE `student_username`=? AND `tutor_username_lesson`=? AND `start_date_time`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getStatus().toString());

            if (item.checkPayment() != null && item.checkPayment().getStripe() != null) {
                stmt.setString(2, item.checkPayment().getStripe().getId());
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            }

            stmt.setString(3, item.checkStudent().getUsername());
            stmt.setString(4, item.checkLesson().getTutor().getUsername());
            stmt.setTimestamp(5, Timestamp.valueOf(item.checkLesson().getStartTime()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error updating reservation", e);
            throw new DAOException("Can't update reservation");
        }
    }

    @Override
    public void delete(Reservation item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Reservation getReservation(String studentUsername, String tutorUsername, LocalDateTime start) throws DAOException {
        getAll();
        for (Reservation r : cache) {
            if (r.checkStudent().getUsername().equals(studentUsername)
                    && r.checkTutor().getUsername().equals(tutorUsername)
                    && r.checkLesson().getStartTime().equals(start)) {
                return r;
            }
        }
        return null;
    }
}