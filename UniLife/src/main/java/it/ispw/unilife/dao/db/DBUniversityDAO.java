package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.UniversityDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.University;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUniversityDAO implements UniversityDAO {

    private static final Logger logger = Logger.getLogger(DBUniversityDAO.class.getName());
    private static DBUniversityDAO instance = null;
    private final List<University> cache = new ArrayList<>();

    private DBUniversityDAO() throws DAOException {
        try{
            getAll();
        }catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static DBUniversityDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new DBUniversityDAO();
        }
        return instance;
    }


    @Override
    public University getUniversity(String uniName) throws DAOException {
        getAll();
        for (University university : cache) {
            if (university.getName().equals(uniName)) {
                return university;
            }
        }
        return null;
    }

    @Override
    public List<University> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        String query = "SELECT `name`, `location`, `ranking`, `living_cost` FROM `university`";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String universityName = rs.getString("name");
                String location = rs.getString("location");
                int ranking = rs.getInt("ranking");
                double livingCost = rs.getDouble("living_cost");

                University university = new University(universityName, location, ranking, livingCost);
                cache.add(university);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error reading applications", e);
            throw new DAOException("Can't load applications");
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(University item) throws DAOException {
        String query = "INSERT INTO `university`(`name`, `location`, `ranking`, `living_cost`) VALUES (?, ?, ?, ?)";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getName());
            stmt.setString(2, item.getLocation());
            stmt.setInt(3, item.getRanking());
            stmt.setDouble(4, item.getLivingCosts());

            stmt.executeUpdate();

            cache.add(item);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error inserting university", e);
            throw new DAOException("Can't insert university");
        }
    }

    @Override
    public void update(University item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(University item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
