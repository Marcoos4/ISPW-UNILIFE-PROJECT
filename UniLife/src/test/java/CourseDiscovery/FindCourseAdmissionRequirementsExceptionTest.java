package CourseDiscovery;

import it.ispw.unilife.bean.CourseBean;
import it.ispw.unilife.bean.UniversityBean;
import it.ispw.unilife.controller.CourseDiscoveryAndApplication;
import it.ispw.unilife.dao.*;
import it.ispw.unilife.dao.factory.DAOFactory;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.University;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FindCourseAdmissionRequirementsExceptionTest {

    private CourseDiscoveryAndApplication controller;

    @BeforeEach
    void setUp() throws Exception {
        List<Course> dummyCourses = new ArrayList<>();
        Course c1 = new Course();
        c1.setCourseTitle("Chemistry");
        c1.setUniversity(new University("Oxford", "UK", 5, 2000.0));
        dummyCourses.add(c1);

        injectStubFactory(dummyCourses);
        controller = new CourseDiscoveryAndApplication();
    }

    @AfterEach
    void tearDown() throws Exception {
        resetDAOFactory();
    }

    @Test
    void testFindCourseAdmissionRequirementsThrowsExceptionIfNotFound() {

        CourseBean input = new CourseBean();
        input.setTitle("NonExistent");
        UniversityBean uni = new UniversityBean();
        uni.setName("Nowhere");
        input.setUniversity(uni);


        assertThrows(DAOException.class, () -> {
            controller.findCourseAdmissionRequirements(input);
        });
    }


    private void injectStubFactory(List<Course> courses) throws Exception {
        Field instance = DAOFactory.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, new StubDAOFactory(courses));
    }

    private void resetDAOFactory() throws Exception {
        Field instance = DAOFactory.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    static class StubDAOFactory extends DAOFactory {
        private final List<Course> courses;
        public StubDAOFactory(List<Course> c) { this.courses = c; }
        @Override public CourseDAO getCourseDAO() { return new StubCourseDAO(courses); }
        public UserDAO getUserDAO() { return null; }
        public UniversityDAO getUniversityDAO() { return null; }
        public ReservationDAO getReservationDAO() { return null; }
        public StudentDAO getStudentDAO() { return null; }
        public TutorDAO getTutorDAO() { return null; }
        public LessonDAO getLessonDAO() { return null; }
        public ApplicationDAO getApplicationDAO() { return null; }
        public ReservationNotificationDAO getReservationNotificationDAO() { return null; }
        public NotificationDAO getNotificationDAO() { return null; }
        public UniversityEmployeeDAO getUniversityEmployeeDAO() { return null; }
    }

    static class StubCourseDAO implements CourseDAO {
        private final List<Course> data;
        public StubCourseDAO(List<Course> data) { this.data = data; }
        @Override public List<Course> getAll() { return data; }
        @Override public Course getCourse(String t, String u) { return null; }
        public void insert(Course b) {
            // MOCK
        }
        public void update(Course b) {
            // MOCK
        }
        public void delete(Course b) {
            // MOCK
        }
    }
}