package it.ispw.unilife.controller;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.dao.CourseDAO;
import it.ispw.unilife.dao.factory.DAOFactory;
import it.ispw.unilife.enums.CourseTags;
import it.ispw.unilife.enums.CourseType;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.DataNotFoundException;
import it.ispw.unilife.exception.InvalidTokenException;
import it.ispw.unilife.model.Course;
import it.ispw.unilife.model.University;
import it.ispw.unilife.model.UniversityEmployee;
import it.ispw.unilife.model.User;
import it.ispw.unilife.model.admission.AbstractRequirement;
import it.ispw.unilife.model.admission.DocumentRequirement;
import it.ispw.unilife.model.admission.TextRequirement;
import it.ispw.unilife.model.session.SessionManager;

import java.util.logging.Logger;

public class ManageCourse {

    private static final Logger LOGGER = Logger.getLogger(ManageCourse.class.getName());


    public void addCourse(TokenBean tokenBean, CourseBean courseBean) throws InvalidTokenException, DAOException, DataNotFoundException {

        UniversityEmployee employee = getAuthorizedEmployee(tokenBean);


        Course newCourse = initBaseCourse(courseBean, employee.getUniversity());

        enrichCourseWithEnums(newCourse, courseBean);


        processAdmissionRequirements(newCourse, courseBean);


        CourseDAO courseDAO = DAOFactory.getDAOFactory().getCourseDAO();
        courseDAO.insert(newCourse);
        LOGGER.info("New course added successfully");
    }


    private UniversityEmployee getAuthorizedEmployee(TokenBean tokenBean) throws InvalidTokenException, DataNotFoundException {
        if (!SessionManager.getInstance().sessionIsValid(tokenBean.getToken())) {
            throw new InvalidTokenException("Invalid session");
        }

        User user = SessionManager.getInstance().getSession(tokenBean.getToken()).getUser();
        if (!(user instanceof UniversityEmployee)) {
            throw new InvalidTokenException("User is not authorized to create courses");
        }

        UniversityEmployee employee = (UniversityEmployee) user;
        if (employee.getUniversity() == null) {
            throw new DataNotFoundException("Employee is not associated with any university");
        }
        return employee;
    }

 
    private Course initBaseCourse(CourseBean bean, University university) {
        Course course = new Course();
        course.setCourseTitle(bean.getTitle());
        course.setCourseDescription(bean.getDescription());
        course.setCourseDuration(bean.getDuration());
        course.setLanguageOfInstruction(bean.getLanguageOfInstruction());
        course.setCourseFees(bean.getFees());
        course.setUniversity(university);
        return course;
    }


    private void enrichCourseWithEnums(Course course, CourseBean bean) throws InvalidTokenException {

        if (bean.getCourseType() != null) {
            try {
                course.setCourseType(CourseType.valueOf(bean.getCourseType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidTokenException("Invalid course type: " + bean.getCourseType());
            }
        }


        if (bean.getTags() != null) {
            for (String tagName : bean.getTags()) {
                try {
                    course.addTag(CourseTags.valueOf(tagName));
                } catch (IllegalArgumentException e) {
                    // ingore invalid tag
                }
            }
        }
    }


    private void processAdmissionRequirements(Course course, CourseBean bean) {
        if (bean.getAdmissionRequirement() == null || bean.getAdmissionRequirement().getRequirements() == null) {
            return;
        }

        for (RequirementBean reqBean : bean.getAdmissionRequirement().getRequirements()) {
            AbstractRequirement reqModel = createRequirementModel(reqBean);
            if (reqModel != null) {
                course.addRequirement(reqModel);
            }
        }
    }

    private AbstractRequirement createRequirementModel(RequirementBean reqBean) {

        String safeName = toUpperSafe(reqBean.getName());
        String safeLabel = toUpperSafe(reqBean.getLabel());
        String safeDesc = toUpperSafe(reqBean.getDescription());

        if ("TEXT".equalsIgnoreCase(reqBean.getType())) {
            return new TextRequirement(
                    safeName, safeLabel, safeDesc,
                    reqBean.getMinChars(), reqBean.getMaxChars()
            );
        } else if ("DOCUMENT".equalsIgnoreCase(reqBean.getType())) {
            return new DocumentRequirement(
                    safeName, safeLabel, safeDesc,
                    toUpperSafe(reqBean.getAllowedExtension()),
                    (long) reqBean.getMaxSizeMB(),
                    reqBean.isCertificate()
            );
        }
        return null;
    }


    private String toUpperSafe(String input) {
        return (input != null) ? input.toUpperCase() : "";
    }


}

