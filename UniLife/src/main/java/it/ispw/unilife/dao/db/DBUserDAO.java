package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.UserDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Student;
import it.ispw.unilife.model.Tutor;
import it.ispw.unilife.model.UniversityEmployee;
import it.ispw.unilife.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUserDAO implements UserDAO {

    private static final Logger logger = Logger.getLogger(DBUserDAO.class.getName());
    private static DBUserDAO instance = null;
    private final List<User> cache = new ArrayList<>();

    private DBUserDAO() throws DAOException {
        try{
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static DBUserDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new DBUserDAO();
        }
        return instance;
    }

    @Override
    public List<User> getAll() throws DAOException {
        cache.addAll(DBStudentDAO.getInstance().getAll());
        cache.addAll(DBTutorDAO.getInstance().getAll());
        cache.addAll(DBUniversityEmployeeDAO.getInstance().getAll());
        return cache;
    }


    @Override
    public void insert(User item) throws DAOException {
// Inserisce i dati comuni nella tabella user.
// I dati specifici (budget, rating, university) dovrebbero essere gestiti
// da DAO specifici o estendendo questa query se hai una tabella unica.
        String query = "INSERT INTO user (username, name, surname, password, role) VALUES (?, ?, ?, ?, ?)";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getUsername());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getSurname());
            stmt.setString(4, item.getPassword());
// Usa name() per compatibilità SQL standard, oppure toString() se hai logica custom
            stmt.setString(5, item.getRole().toString());

            stmt.executeUpdate();
            switch (item.getRole()) {
                case TUTOR -> DBTutorDAO.getInstance().insert((Tutor) item);
                case STUDENT -> DBStudentDAO.getInstance().insert((Student) item);
                case UNIVERSITY_EMPLOYEE -> DBUniversityEmployeeDAO.getInstance().insert((UniversityEmployee) item);
            }
            cache.add(item);

        } catch (SQLException e) {
            throw new DAOException("Can't insert user: " + e.getMessage());
        }
    }

    @Override
    public void update(User item) throws DAOException {
        String query = "UPDATE user SET name = ?, surname = ?, password = ?, role = ? WHERE username = ?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getName());
            stmt.setString(2, item.getSurname());
            stmt.setString(3, item.getPassword());
            stmt.setString(4, item.getRole().name());
            stmt.setString(5, item.getUsername());

            int rows = stmt.executeUpdate();
            switch (item.getRole()) {
                case TUTOR -> DBTutorDAO.getInstance().update((Tutor) item);
                case STUDENT -> DBStudentDAO.getInstance().update((Student) item);
                case UNIVERSITY_EMPLOYEE -> DBUniversityEmployeeDAO.getInstance().update((UniversityEmployee) item);
            }
            if (rows == 0) {
                throw new DAOException("User to update not found");
            }
            cache.removeIf(u -> u.getUsername().equals(item.getUsername()));
            cache.add(item);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in update user", e);
            throw new DAOException("Can't update user");
        }
    }

    @Override
    public void delete(User item) throws DAOException {
        String query = "DELETE FROM user WHERE username = ?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getUsername());
            stmt.executeUpdate();
            switch (item.getRole()) {
                case TUTOR -> DBTutorDAO.getInstance().delete((Tutor) item);
                case STUDENT -> DBStudentDAO.getInstance().delete((Student) item);
                case UNIVERSITY_EMPLOYEE -> DBUniversityEmployeeDAO.getInstance().delete((UniversityEmployee) item);
            }
            cache.removeIf(u -> u.getUsername().equals(item.getUsername()));

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in delete user", e);
            throw new DAOException("Can't delete user");
        }
    }

    @Override
    public User getUser(String username) throws UserNotFoundException, DAOException {
        // Logica standard
        getAll();
        for(User u : cache) if(u.getUsername().equals(username)) return u;
        throw new UserNotFoundException();
    }
}