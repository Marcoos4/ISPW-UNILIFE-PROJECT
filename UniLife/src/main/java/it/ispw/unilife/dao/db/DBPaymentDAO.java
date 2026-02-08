package it.ispw.unilife.dao.db;

import com.stripe.model.PaymentIntent;
import it.ispw.unilife.dao.PaymentDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.enums.PaymentStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBPaymentDAO implements PaymentDAO {

    private static final Logger logger = Logger.getLogger(DBPaymentDAO.class.getName());
    private static DBPaymentDAO instance = null;

    private DBPaymentDAO() {
    }

    public static DBPaymentDAO getInstance() {
        if (instance == null) {
            instance = new DBPaymentDAO();
        }
        return instance;
    }

    public Payment getPayment(String stripe) throws DAOException {
        if (stripe == null) return null;

        String query = "SELECT `amount`, `status` FROM `payment` WHERE `payment_stripe`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, stripe);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    float amount = rs.getFloat("amount");
                    String statusStr = rs.getString("status");

                    PaymentStatus status = PaymentStatus.fromString(statusStr);

                    PaymentIntent stripeObj = new PaymentIntent();
                    stripeObj.setId(stripe);

                    return new Payment(amount, stripeObj, status);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error fetching payment", e);
            throw new DAOException("Can't load payment");
        }
        return null;
    }

    @Override
    public void insert(Payment item) throws DAOException {
        if (item.getStripe() == null || item.getStripe().getId() == null) {
            throw new DAOException("Cannot insert Payment without a valid Stripe ID");
        }

        String query = "INSERT INTO `payment`(`amount`, `status`, `payment_stripe`) VALUES (?, ?, ?)";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setFloat(1, item.showCost());
            stmt.setString(2, item.getStatus().toString());
            stmt.setString(3, item.getStripe().getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error inserting payment", e);
            throw new DAOException("Can't insert payment");
        }
    }

    @Override
    public void update(Payment item) throws DAOException {
        if (item.getStripe() == null || item.getStripe().getId() == null) {
            throw new DAOException("Cannot update Payment without a valid Stripe ID");
        }

        String query = "UPDATE `payment` SET `amount`=?, `status`=? WHERE `payment_stripe`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setFloat(1, item.showCost());
            stmt.setString(2, item.getStatus().toString());
            stmt.setString(3, item.getStripe().getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error updating payment", e);
            throw new DAOException("Can't update payment");
        }
    }

    @Override
    public void delete(Payment item) throws DAOException {
        if (item.getStripe() == null || item.getStripe().getId() == null) return;

        String query = "DELETE FROM `payment` WHERE `payment_stripe`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getStripe().getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error deleting payment", e);
            throw new DAOException("Can't delete payment");
        }
    }

    @Override
    public List<Payment> getAll() throws DAOException {
        return List.of();
    }

}