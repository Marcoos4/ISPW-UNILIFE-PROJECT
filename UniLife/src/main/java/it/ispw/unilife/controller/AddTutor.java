package it.ispw.unilife.controller;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.dao.LessonDAO;
import it.ispw.unilife.dao.factory.DAOFactory;
import it.ispw.unilife.enums.LessonStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.EmployeeNotFoundException;
import it.ispw.unilife.exception.InvalidTokenException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.Lesson;
import it.ispw.unilife.model.Tutor;
import it.ispw.unilife.model.User;
import it.ispw.unilife.model.session.Session;
import it.ispw.unilife.model.session.SessionManager;

import java.util.List;

public class AddTutor {

    public void startTutorLessonProcedure(TokenBean tokenBean, LessonBean lessonBean) throws EmployeeNotFoundException, InvalidTokenException, DAOException {
        User currentUser = getActiveUser(tokenBean);
        if (currentUser == null) throw new EmployeeNotFoundException();
        Lesson lesson = convertLessonBeanToLesson(lessonBean, (Tutor) currentUser);
        LessonDAO lessonDAO = DAOFactory.getDAOFactory().getLessonDAO();
        lessonDAO.insert(lesson);
        lesson.createLesson(tokenBean.getToken());
    }


    public void acceptLesson(TokenBean tokenBean, LessonBean lessonBean) throws DAOException {
        Lesson lesson = convertLessonBeanToLesson(lessonBean);
        lesson.setStatus(LessonStatus.ACCEPTED, tokenBean.getToken());
        LessonDAO lessonDAO = DAOFactory.getDAOFactory().getLessonDAO();
        lessonDAO.update(lesson);
    }

    public void rejectLesson(TokenBean tokenBean, LessonBean lessonBean) throws DAOException {
        Lesson lesson = convertLessonBeanToLesson(lessonBean);
        lesson.setStatus(LessonStatus.REJECTED, tokenBean.getToken());
        LessonDAO lessonDAO = DAOFactory.getDAOFactory().getLessonDAO();
        lessonDAO.update(lesson);
    }


    private Lesson convertLessonBeanToLesson(LessonBean lessonBean) throws DAOException {
        LessonDAO lessonDAO = DAOFactory.getDAOFactory().getLessonDAO();
        List<Lesson> lessons = null;
        try {
            lessons = lessonDAO.getAll();
        } catch (UserNotFoundException e) {
            throw new DAOException(e.getMessage());
        }
        for (Lesson lesson : lessons) {
            if (lesson.getStartTime().equals(lessonBean.getStartTime())&& lesson.getTutor().getUsername().equals(lessonBean.getTutor().getUsername())) {
                return lesson;
            }
        }

        throw new DAOException("Lesson non trovata");

    }

    private Lesson convertLessonBeanToLesson(LessonBean lessonBean, Tutor tutor) {
        return new Lesson(lessonBean.getSubject(), lessonBean.getStartTime(), lessonBean.getEndTime(), lessonBean.getPrice(), tutor, LessonStatus.PENDING);

    }


    private User getActiveUser(TokenBean tokenBean) throws InvalidTokenException {
        if (!SessionManager.getInstance().sessionIsValid(tokenBean.getToken())) {
            throw new InvalidTokenException("Token non valido");
        }
        Session session = SessionManager.getInstance().getSession(tokenBean.getToken());
        return session.getUser();
    }


}
