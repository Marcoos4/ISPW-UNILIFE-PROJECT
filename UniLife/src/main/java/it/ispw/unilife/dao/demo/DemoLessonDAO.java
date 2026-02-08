package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.LessonDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Lesson;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DemoLessonDAO implements LessonDAO {

    private static DemoLessonDAO instance = null;
    private final List<Lesson> cache = new ArrayList<>();

    private DemoLessonDAO() {}

    public static DemoLessonDAO getInstance() {
        if (instance == null) {
            instance = new DemoLessonDAO();
        }
        return instance;
    }

    @Override
    public Lesson getLesson(String username, LocalDateTime creationDate) throws DAOException {
        LocalDateTime searchDate = creationDate.truncatedTo(ChronoUnit.SECONDS);
        for (Lesson lesson : cache) {
            LocalDateTime lessonDate = lesson.getStartTime().truncatedTo(ChronoUnit.SECONDS);
            if (lesson.getTutor().getUsername().equals(username) && lessonDate.equals(searchDate)) {
                return lesson;
            }
        }
        return null;
    }

    @Override
    public List<Lesson> getAll() throws DAOException {
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Lesson item) throws DAOException {
        cache.add(item);
    }

    @Override
    public void update(Lesson item) throws DAOException {
        cache.removeIf(l ->
                l.getTutor().getUsername().equals(item.getTutor().getUsername()) &&
                        l.getStartTime().equals(item.getStartTime()));
        cache.add(item);
    }

    @Override
    public void delete(Lesson item) throws DAOException {
        cache.removeIf(l ->
                l.getTutor().getUsername().equals(item.getTutor().getUsername()) &&
                        l.getStartTime().equals(item.getStartTime()));
    }
}
