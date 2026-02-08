package it.ispw.unilife.dao.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.ispw.unilife.dao.ReservationDAO;
import it.ispw.unilife.enums.ReservationStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.*;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONReservationDAO implements ReservationDAO {

    private static final Logger logger = Logger.getLogger(JSONReservationDAO.class.getName());
    private static final String FILE_NAME = "reservations.json";
    private static JSONReservationDAO instance = null;
    private final List<Reservation> cache = new ArrayList<>();

    private JSONReservationDAO() throws DAOException {
        try {
            getAll();
        } catch (DAOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    public static synchronized JSONReservationDAO getInstance() throws DAOException {
        if (instance == null) {
            instance = new JSONReservationDAO();
        }
        return instance;
    }

    private void saveToFile() {
        Gson gson = JsonUtil.getGson();
        List<JsonRecords.ReservationRecord> records = new ArrayList<>();
        for (Reservation r : cache) {
            JsonRecords.ReservationRecord rec = new JsonRecords.ReservationRecord();
            rec.setStudentUsername(r.checkStudent().getUsername());
            rec.setTutorUsername(r.checkTutor().getUsername());
            rec.setStartDateTime(r.checkLesson().getStartTime());
            rec.setStatus(r.getStatus().toString());

            if (r.checkPayment() != null && r.checkPayment().getStripe() != null) {
                rec.setPaymentStripe(r.checkPayment().getStripe().getId());
            } else {
                rec.setPaymentStripe(null);
            }

            records.add(rec);
        }
        JsonUtil.writeFile(JsonUtil.getFile(FILE_NAME), gson.toJson(records));
    }

    @Override
    public Reservation getReservation(String studentUsername, String tutorUsername, LocalDateTime start) throws DAOException {
        if (cache.isEmpty()) {
            getAll();
        }
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
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache);
        }

        Gson gson = JsonUtil.getGson();
        File file = JsonUtil.getFile(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        String json = JsonUtil.readFile(file);

        Type listType = new TypeToken<List<JsonRecords.ReservationRecord>>() {}.getType();
        List<JsonRecords.ReservationRecord> records = gson.fromJson(json, listType);

        if (records != null) {
            for (JsonRecords.ReservationRecord r : records) {
                try {
                    Student student = JSONStudentDAO.getInstance().getStudent(r.getStudentUsername());
                    Lesson lesson = JSONLessonDAO.getInstance().getLesson(r.getTutorUsername(), r.getStartDateTime());
                    Payment payment = JSONPaymentDAO.getInstance().getPayment(r.getPaymentStripe());
                    Reservation reservation = new Reservation(student, lesson, payment, ReservationStatus.fromString(r.getStatus()));
                    cache.add(reservation);
                } catch (UserNotFoundException e) {
                    logger.log(Level.SEVERE, "User not found", e);
                }
            }
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void insert(Reservation item) throws DAOException {
        if (item.checkPayment() != null && item.checkPayment().getStripe() != null) {
            JSONPaymentDAO.getInstance().insert(item.checkPayment());
        }
        cache.add(item);
        saveToFile();
    }

    @Override
    public void update(Reservation item) throws DAOException {
        if (item.checkPayment() != null && item.checkPayment().getStripe() != null) {
            String stripeId = item.checkPayment().getStripe().getId();
            Payment existing = JSONPaymentDAO.getInstance().getPayment(stripeId);
            if (existing == null) {
                JSONPaymentDAO.getInstance().insert(item.checkPayment());
            } else {
                JSONPaymentDAO.getInstance().update(item.checkPayment());
            }
        }
        cache.removeIf(r ->
                r.checkStudent().getUsername().equals(item.checkStudent().getUsername()) &&
                        r.checkLesson().getTutor().getUsername().equals(item.checkLesson().getTutor().getUsername()) &&
                        r.checkLesson().getStartTime().equals(item.checkLesson().getStartTime()));
        cache.add(item);
        saveToFile();
    }

    @Override
    public void delete(Reservation item) throws DAOException {
        cache.removeIf(r ->
                r.checkStudent().getUsername().equals(item.checkStudent().getUsername()) &&
                        r.checkLesson().getTutor().getUsername().equals(item.checkLesson().getTutor().getUsername()) &&
                        r.checkLesson().getStartTime().equals(item.checkLesson().getStartTime()));
        saveToFile();
    }
}