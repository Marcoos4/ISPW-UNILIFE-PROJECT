package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.DocumentDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Document;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBDocumentDAO implements DocumentDAO {
    private static final Logger logger = Logger.getLogger(DBDocumentDAO.class.getName());
    private static DBDocumentDAO instance = null;
    private final List<Document> cache = new ArrayList<>();

    private DBDocumentDAO() {
        try{
            getAll();
        }catch (DAOException e) {
            logger.severe(e.getMessage());
        }
    }

    public static DBDocumentDAO getInstance() {
        if (instance == null) {
            instance = new DBDocumentDAO();
        }
        return instance;
    }


    @Override
    public List<Document> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        String query = "SELECT `name`, `type`, `size`, `content` FROM `document`";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");
                double size = rs.getInt("size");
                byte[] content = rs.getBytes("content");

                Document document = new Document(name, type, size, content);

                cache.add(document);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error reading applications", e);
            throw new DAOException("Can't load applications");
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Document item) throws DAOException {
        String query = "INSERT INTO `document`(`name`, `type`, `size`, `content`) VALUES (?, ?, ?, ?)";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getFileName());
            stmt.setString(2, item.getFileType());
            stmt.setDouble(3, item.getFileSize());
            stmt.setBytes(4, item.getContent());

            stmt.executeUpdate();
            cache.add(item);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in insert application", e);
            throw new DAOException("Can't insert application");
        }

    }

    @Override
    public void update(Document item) throws DAOException {
        String query = "UPDATE document SET name = ?, type = ?, size = ?, content = ? WHERE name = ?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getFileName());
            stmt.setString(2, item.getFileType());
            stmt.setDouble(3, item.getFileSize());
            stmt.setBytes(4, item.getContent());
            stmt.setString(5, item.getFileName());

            stmt.executeUpdate();
            cache.add(item);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in update application", e);
            throw new DAOException("Can't insert application");
        }
    }

    @Override
    public void delete(Document item) throws DAOException {

        String query = "DELETE document WHERE name = ? AND type = ? AND size = ? AND content = ?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getFileName());
            stmt.setString(2, item.getFileType());
            stmt.setDouble(3, item.getFileSize());
            stmt.setBytes(4, item.getContent());

            stmt.executeUpdate();
            cache.add(item);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in delete application", e);
            throw new DAOException("Can't insert application");
        }
    }

    @Override
    public Document getDocument(String documentName) throws DAOException {
        getAll();
        for (Document document : cache) {
            if (document.getFileName().equals(documentName)) {
                return document;
            }
        }
        return null;
    }
}
