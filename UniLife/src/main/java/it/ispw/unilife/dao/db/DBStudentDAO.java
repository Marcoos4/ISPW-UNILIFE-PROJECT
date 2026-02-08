package it.ispw.unilife.dao.db;

import it.ispw.unilife.dao.StudentDAO;
import it.ispw.unilife.dao.factory.ConnectionFactory;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.enums.Role;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBStudentDAO implements StudentDAO {

    private static final Logger logger = Logger.getLogger(DBStudentDAO.class.getName());
    private static DBStudentDAO instance = null;
    private final List<Student> cache = new ArrayList<>();

    private DBStudentDAO() throws DAOException {
        try{
            getAll();
        }catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static DBStudentDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new DBStudentDAO();
        }
        return instance;
    }

    @Override
    public List<Student> getAll() throws DAOException {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        String query = "SELECT s.username, s.budget, u.name, u.surname, u.password" +
                " FROM student s JOIN user u ON s.username = u.username WHERE u.role = ?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, Role.STUDENT.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                Student student = new Student(username, rs.getString("name"), rs.getString("surname"), rs.getString("password"));
                student.setBudget(rs.getDouble("budget"));
                List<Course> courses = DBInterestedStudentDAO.getInstance().getInterestedCourse(username);
                student.updateStarredCourses(courses);
                List<CourseTags> interests = DBStudentInterestDAO.getInstance().getStudentInterests(username);
                student.updateInterests(interests);
                cache.add(student);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in getAll students", e);
            throw new DAOException("Can't get Student list from DB");
        }
        return new ArrayList<>(cache);
    }


    @Override
    public Student getStudent(String username) throws UserNotFoundException, DAOException {
        getAll();
        for (Student student : getAll()) {
            if (student.getUsername().equals(username)) {
                return student;
            }
        }

        throw new UserNotFoundException();
    }

    @Override
    public void insert(Student item) throws DAOException {
        String query = "INSERT INTO `student`(`username`, `budget`) VALUES (?, ?)";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getUsername());
            stmt.setDouble(2, item.getBudget());
            stmt.executeUpdate();
            for (Course course : item.getStarredCourses()) {
                DBInterestedStudentDAO.getInstance().insert(item, course);
            }
            for (CourseTags courseTags : item.getInterests()) {
                DBStudentInterestDAO.getInstance().insert(courseTags, item);
            }

            cache.add(item);
            logger.info("Studente salvato correttamente nel database!");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in insert student", e);
            throw new DAOException("Can't insert student");
        }
    }

    @Override
    public void update(Student item) throws DAOException {
        // No additional student-specific fields to update
    }

    @Override
    public void delete(Student item) throws DAOException {
        if (item == null || item.getUsername() == null) {
            throw new DAOException("Student is null");
        }

        String query = "DELETE FROM student WHERE username = ?";
        Connection conn = ConnectionFactory.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, item.getUsername());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                cache.removeIf(s -> s.getUsername().equals(item.getUsername()));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "DB Error in delete student", e);
            throw new DAOException("Can't delete student");
        }
    }

}
