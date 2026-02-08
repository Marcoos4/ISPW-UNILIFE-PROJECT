package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.TutorDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.enums.Role;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Tutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBTutorDAO implements TutorDAO {

    private static final Logger logger = Logger.getLogger(DBTutorDAO.class.getName());
    private static DBTutorDAO instance = null;
    private final List<Tutor> cache = new ArrayList<>();

    private DBTutorDAO() throws DAOException {
        try{
            getAll();
        }catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static DBTutorDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new DBTutorDAO();
        }
        return instance;
    }

    @Override
    public List<Tutor> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        String query = "SELECT t.username, t.rating, u.name, u.surname, u.password" +
                " FROM tutor t JOIN user u ON t.username = u.username WHERE u.role = ?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, Role.TUTOR.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Tutor tutor = new Tutor(rs.getString("username"), rs.getString("name"), rs.getString("surname"), rs.getString("password"));
                tutor.updateRating(rs.getInt("rating"));
                cache.add(tutor);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in getAll tutors", e);
            throw new DAOException("Can't get Tutor list from DB");
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Tutor item) throws DAOException {
        String query = "INSERT INTO tutor (username, rating) VALUES (?, ?)";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getUsername());
            stmt.setFloat(2, item.getRating());

            stmt.executeUpdate();
            cache.add(item);
            logger.info("Tutor salvato correttamente nel database!");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in insert tutor", e);
            throw new DAOException("Can't insert tutor");
        }
    }

    @Override
    public void update(Tutor item) throws DAOException {
        String query = "UPDATE tutor SET hourlyrate = ?, rating = ? WHERE username = ?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setFloat(1, item.getRating());
            stmt.setFloat(2, item.getRating());
            stmt.setString(3, item.getUsername());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                cache.removeIf(t -> t.getUsername().equals(item.getUsername()));
                cache.add(item);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in update tutor", e);
            throw new DAOException("Can't update tutor");
        }
    }

    @Override
    public void delete(Tutor item) throws DAOException {
        if (item == null || item.getUsername() == null) {
            throw new DAOException("Tutor is null");
        }

        String query = "DELETE FROM tutor WHERE username = ?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getUsername());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                cache.removeIf(t -> t.getUsername().equals(item.getUsername()));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in delete tutor", e);
            throw new DAOException("Can't delete tutor");
        }
    }

    private Tutor checkCache(String username) {
        for (Tutor tutor : cache) {
            if (tutor.getUsername().equalsIgnoreCase(username)) {
                return tutor;
            }
        }
        return null;
    }

    @Override
    public Tutor getTutor(String username) throws UserNotFoundException, DAOException {
        getAll();
        if (checkCache(username) != null) {
            return checkCache(username);
        }
        return null;
    }
}
