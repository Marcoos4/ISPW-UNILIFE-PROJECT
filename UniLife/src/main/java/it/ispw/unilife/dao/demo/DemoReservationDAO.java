package it.ispw.unilife.dao.demo;

import it.ispw.unilife.dao.ReservationDAO;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.model.Reservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DemoReservationDAO implements ReservationDAO {

    private static DemoReservationDAO instance = null;
    private final List<Reservation> cache = new ArrayList<>();

    private DemoReservationDAO() {}

    public static DemoReservationDAO getInstance() {
        if (instance == null) {
            instance = new DemoReservationDAO();
        }
        return instance;
    }

    @Override
    public Reservation getReservation(String studentUsername, String tutorUsername, LocalDateTime start) throws DAOException {
        for (Reservation r : cache) {
            if (r.checkStudent().getUsername().equals(studentUsername)
                    && r.checkTutor().getUsername().equals(tutorUsername)
                    && r.checkLesson().getStartTime().equals(start)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public List<Reservation> getAll() throws DAOException {
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Reservation item) throws DAOException {
        cache.add(item);
        DemoPaymentDAO.getInstance().insert(item.checkPayment());
    }

    @Override
    public void update(Reservation item) throws DAOException {
        cache.removeIf(r ->
                r.checkStudent().getUsername().equals(item.checkStudent().getUsername()) &&
                        r.checkLesson().getTutor().getUsername().equals(item.checkLesson().getTutor().getUsername()) &&
                        r.checkLesson().getStartTime().equals(item.checkLesson().getStartTime()));
        cache.add(item);
        DemoPaymentDAO.getInstance().update(item.checkPayment());
    }

    @Override
    public void delete(Reservation item) throws DAOException {
        cache.removeIf(r ->
                r.checkStudent().getUsername().equals(item.checkStudent().getUsername()) &&
                        r.checkLesson().getTutor().getUsername().equals(item.checkLesson().getTutor().getUsername()) &&
                        r.checkLesson().getStartTime().equals(item.checkLesson().getStartTime()));
        DemoPaymentDAO.getInstance().delete(item.checkPayment());
    }
}
