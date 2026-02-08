package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.UniversityEmployeeDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.enums.Role;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.UniversityEmployee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUniversityEmployeeDAO implements UniversityEmployeeDAO {

    private static final Logger logger = Logger.getLogger(DBUniversityEmployeeDAO.class.getName());
    private static DBUniversityEmployeeDAO instance = null;
    private final List<UniversityEmployee> cache = new ArrayList<>();

    private DBUniversityEmployeeDAO() throws DAOException {
        try{
            getAll();
        }catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static DBUniversityEmployeeDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new DBUniversityEmployeeDAO();
        }
        return instance;
    }

    @Override
    public List<UniversityEmployee> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        String query = "SELECT ue.username, ue.university_name, u.name, u.surname, u.password" +
                " FROM university_employee ue JOIN user u ON ue.username = u.username WHERE u.role = ?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, Role.UNIVERSITY_EMPLOYEE.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UniversityEmployee universityEmployee = new UniversityEmployee(rs.getString("username"), rs.getString("name"), rs.getString("surname"), rs.getString("password"));
                universityEmployee.setUniversity(DBUniversityDAO.getInstance().getUniversity(rs.getString("university_name")));
                cache.add(universityEmployee);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error reading applications", e);
            throw new DAOException("Can't load applications");
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(UniversityEmployee item) throws DAOException {
        String query = "INSERT INTO `university_employee`(`username`, `university_name`) VALUES (?, ?)";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getUsername());
            stmt.setString(2, item.getUniversity().getName());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error reading applications", e);
            throw new DAOException("Can't load applications");
        }

    }

    @Override
    public void update(UniversityEmployee item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(UniversityEmployee item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
