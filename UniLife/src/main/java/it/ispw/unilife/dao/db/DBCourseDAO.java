package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.CourseDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.enums.CourseType;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.admission.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBCourseDAO implements CourseDAO {

    private static final Logger logger = Logger.getLogger(DBCourseDAO.class.getName());
    private static DBCourseDAO instance = null;
    private final List<Course> cache = new ArrayList<>();

    private DBCourseDAO() throws DAOException {
        try{
            getAll();
        }catch (DAOException e) {
            throw new DAOException("Course problem");
        }
    }

    public static DBCourseDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new DBCourseDAO();
        }
        return instance;
    }


    @Override
    public Course getCourse(String courseTitle, String universityName) throws DAOException {
        if(cache.isEmpty()){
            getAll();
        }
        for (Course course : cache) {
            if (course.getCourseTitle().equals(courseTitle) && course.getUniversity().getName().equals(universityName)) {
                return course;
            }
        }
        throw new DAOException("Course not found");
    }

    @Override
    public List<Course> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        String query = "SELECT `title`, `description`, `university_name`, `duration`, `fees`, `course_type`, `language` FROM `course`";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String courseTitle = rs.getString("title");
                String universityName = rs.getString("university_name");
                int duration = rs.getInt("duration");
                double fees = rs.getDouble("fees");
                CourseType courseType = CourseType.fromString(rs.getString("course_type"));
                String description = rs.getString("description");
                String language = rs.getString("language");

                AdmissionRequirements admissionRequirements = new AdmissionRequirements();

                List<DocumentRequirement> documentRequirements = DBDocumentRequirementDAO.getInstance().getDocumentRequirements(courseTitle, universityName);
                List<TextRequirement> textRequirements = DBTextRequirementDAO.getInstance().getTextRequirements(courseTitle, universityName);
                for(DocumentRequirement documentRequirement : documentRequirements){
                    admissionRequirements.addRequirement(documentRequirement);
                }

                for(TextRequirement textRequirement : textRequirements){
                    admissionRequirements.addRequirement(textRequirement);
                }

                List<CourseTags> courseTags = DBCourseTagDAO.getInstance().getCourseTags(courseTitle, universityName);

                Course course = new Course(courseTitle, description, courseType, language,
                        DBUniversityDAO.getInstance().getUniversity(universityName), courseTags,  admissionRequirements);

                course.setCourseFees(fees);
                course.setCourseDuration(duration);

                cache.add(course);

            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error reading course", e);
            throw new DAOException("Can't load applications");
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Course item) throws DAOException {
        String query = "INSERT INTO `course`(`title`, `description`, `university_name`, `duration`, `fees`, `course_type`, `language`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1,item.getCourseTitle());
            stmt.setString(2,item.getCourseDescription());
            stmt.setString(3,item.getUniversity().getName());
            stmt.setInt(4, item.getCourseDuration());
            stmt.setDouble(5, item.getCourseFees());
            stmt.setString(6, item.getCourseType().toString());
            stmt.setString(7, item.getLanguageOfInstruction());

            stmt.executeUpdate();

            for (CourseTags courseTags: item.getTags()){
                DBCourseTagDAO.getInstance().insert(courseTags, item);
            }
            for (AbstractRequirement abstractRequirement: item.getRequirements()){
                if(abstractRequirement instanceof TextRequirement){
                    DBTextRequirementDAO.getInstance().insert((TextRequirement) abstractRequirement, item);
                }
                else if(abstractRequirement instanceof DocumentRequirement){
                    DBDocumentRequirementDAO.getInstance().insert((DocumentRequirement) abstractRequirement, item);
                }
            }

            cache.add(item);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in insert application", e);
            throw new DAOException("Can't insert application");
        }
    }

    @Override
    public void update(Course item) throws DAOException {
        String query = "UPDATE `course` SET `description`=?, `duration`=?, `fees`=?, `course_type`=? WHERE `title`=? AND `university_name`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getCourseDescription());
            stmt.setInt(2, item.getCourseDuration());
            stmt.setDouble(3, item.getCourseFees());
            stmt.setString(4, item.getCourseType().toString());
            stmt.setString(5, item.getCourseTitle());
            stmt.setString(6, item.getUniversity().getName());

            stmt.executeUpdate();

            DBUniversityDAO.getInstance().update(item.getUniversity());

            for (CourseTags courseTags: item.getTags()){
                DBCourseTagDAO.getInstance().update(courseTags, item);
            }
            for (AbstractRequirement abstractRequirement: item.getRequirements()){
                if(abstractRequirement instanceof TextRequirement){
                    DBTextRequirementDAO.getInstance().update((TextRequirement) abstractRequirement, item);
                }
                else if(abstractRequirement instanceof DocumentRequirement){
                    DBDocumentRequirementDAO.getInstance().update((DocumentRequirement) abstractRequirement, item);
                }
            }

            cache.removeIf(c -> c.getCourseTitle().equals(item.getCourseTitle()) && c.getUniversity().getName().equals(item.getUniversity().getName()));
            cache.add(item);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in update course", e);
            throw new DAOException("Can't update course");
        }
    }

    @Override
    public void delete(Course item) throws DAOException {
        String query = "DELETE FROM `course` WHERE `title`=? AND `university_name`=?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, item.getCourseTitle());
            stmt.setString(2, item.getUniversity().getName());

            for (CourseTags courseTags: item.getTags()){
                DBCourseTagDAO.getInstance().delete(courseTags, item);
            }
            for (AbstractRequirement abstractRequirement: item.getRequirements()){
                if(abstractRequirement instanceof TextRequirement){
                    DBTextRequirementDAO.getInstance().delete((TextRequirement) abstractRequirement, item);
                }
                else if(abstractRequirement instanceof DocumentRequirement){
                    DBDocumentRequirementDAO.getInstance().delete((DocumentRequirement) abstractRequirement, item);
                }
            }

            stmt.executeUpdate();

            cache.removeIf(c -> c.getCourseTitle().equals(item.getCourseTitle()) && c.getUniversity().getName().equals(item.getUniversity().getName()));

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in delete course", e);
            throw new DAOException("Can't delete course");
        }
    }
}
