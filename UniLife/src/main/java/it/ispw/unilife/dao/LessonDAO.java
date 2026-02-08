package it.ispw.unilife.dao;

import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Lesson;

import java.time.LocalDateTime;

public interface LessonDAO extends DAO<Lesson> {
    Lesson getLesson(String username, LocalDateTime creationDate) throws DAOException;

}
