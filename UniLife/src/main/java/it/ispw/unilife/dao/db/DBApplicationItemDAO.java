package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.ApplicationItemDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.enums.RequirementType;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.admission.Application;
import it.ispw.unilife.model.admission.ApplicationItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBApplicationItemDAO implements ApplicationItemDAO {

    private static final Logger logger = Logger.getLogger(DBApplicationItemDAO.class.getName());
    private static DBApplicationItemDAO instance = null;
    private final List<ApplicationItem> cache = new ArrayList<>();

    private DBApplicationItemDAO() {
        try{
            getAll();
        }catch (DAOException e) {
            logger.severe(e.getMessage());
        }
    }

    public static DBApplicationItemDAO getInstance() {
        if (instance == null) {
            instance = new DBApplicationItemDAO();
        }
        return instance;
    }

    @Override
    public List<ApplicationItem> getItems(String courseTitle, String universityName, String studentUsername, LocalDateTime creationDate) throws DAOException {
        String query = "SELECT `course_title`, `university_name`, `creation_date`, `student_username`, `requirement_name`, `type`, `text`, `document` FROM `application_item`";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String courseTitle1 = rs.getString("course_title");
                String universityName1 = rs.getString("university_name");
                LocalDateTime creationDate1 = rs.getTimestamp("creation_date").toLocalDateTime();
                String studentUsername1 = rs.getString("student_username");

                String requirementName = rs.getString("requirement_name");

                String type = rs.getString("type");
                String text = rs.getString("text");
                String document = rs.getString("document");

                if (courseTitle1.equals(courseTitle) && universityName1.equals(universityName) && studentUsername1.equals(studentUsername) && creationDate1.equals(creationDate)) {
                    ApplicationItem appItem = new ApplicationItem(requirementName, RequirementType.fromString(type), text, DBDocumentDAO.getInstance().getDocument(document));
                    cache.add(appItem);
                }

            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error reading applications", e);
            throw new DAOException("Can't load applications");
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(ApplicationItem item, Application application) throws DAOException {
        String query = "INSERT INTO `application_item`(`course_title`, `university_name`, `creation_date`, `student_username`, `requirement_name`, `type`, `text`, `document`) VALUES (?,?,?,?,?,?,?,?)";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, application.getCourse().getCourseTitle());
            stmt.setString(2, application.getCourse().getUniversity().getName());
            stmt.setTimestamp(3, Timestamp.valueOf(application.getCreationDate()));
            stmt.setString(4, application.getApplicant().getUsername());
            stmt.setString(5, item.getRequirementName());
            stmt.setString(6, item.getType().toString());
            stmt.setString(7, item.getTextContent());

            if (item.getDocumentContent() != null) {
                stmt.setString(8, item.getDocumentContent().getFileName());
            } else {
                stmt.setNull(8, Types.VARCHAR);
            }

            stmt.executeUpdate();
            cache.add(item);

            logger.log(Level.INFO,"Inserted application item: {} ", item);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in insert application", e);
            throw new DAOException("Can't insert application");
        }
    }

    @Override
    public void update(ApplicationItem item, Application application) throws DAOException {
        String query = "UPDATE `application_item` SET `course_title`=?,`university_name`=?,`creation_date`=?,`student_username`=?,`requirement_name`=?,`type`=?,`text`=?,`document`=? WHERE `course_title`=? AND `university_name`=? AND `student_username`=? AND `requirement_name`=? AND `creation_date`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, application.getCourse().getCourseTitle());
            stmt.setString(2, application.getCourse().getUniversity().getName());
            stmt.setTimestamp(3, Timestamp.valueOf(application.getCreationDate()));
            stmt.setString(4, application.getApplicant().getUsername());
            stmt.setString(5, item.getRequirementName());
            stmt.setString(6, item.getType().toString());
            stmt.setString(7, item.getTextContent());
            stmt.setString(8, item.getDocumentContent().getFileName());
            stmt.setString(9, application.getCourse().getCourseTitle());
            stmt.setString(10, application.getCourse().getUniversity().getName());
            stmt.setString(11, application.getApplicant().getUsername());
            stmt.setString(12, item.getRequirementName());
            stmt.setTimestamp(13, Timestamp.valueOf(application.getCreationDate()));

            stmt.executeUpdate();

            cache.removeIf(a -> a.equals(item));
            cache.add(item);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error updating application", e);
            throw new DAOException("Can't update application");
        }
    }

    @Override
    public void delete(ApplicationItem item, Application application) throws DAOException {

        String query = "DELETE FROM `application_item` WHERE `course_title` = ? AND `university_name` = ? AND `creation_date` = ? AND `student_username` = ? AND `requirement_name` = ? AND `type` = ? AND `text` = ? AND `document` = ?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, application.getCourse().getCourseTitle());
            stmt.setString(2, application.getCourse().getUniversity().getName());
            stmt.setTimestamp(3, Timestamp.valueOf(application.getCreationDate()));
            stmt.setString(4, application.getApplicant().getUsername());
            stmt.setString(5, item.getRequirementName());
            stmt.setString(6, item.getType().toString());
            stmt.setString(7, item.getTextContent());
            stmt.setString(8, item.getDocumentContent().getFileName());

            stmt.executeUpdate();

            cache.removeIf(a -> a.equals(item));

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error updating application", e);
            throw new DAOException("Can't update application");
        }

    }

    @Override
    public List<ApplicationItem> getAll() throws DAOException {
        return List.of();
    }

    @Override
    public void insert(ApplicationItem item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(ApplicationItem item) throws DAOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(ApplicationItem item) throws DAOException {
        throw  new UnsupportedOperationException("Not supported yet.");
    }
}
