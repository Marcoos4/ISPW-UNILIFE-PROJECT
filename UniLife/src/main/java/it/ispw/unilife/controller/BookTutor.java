package it.ispw.unilife.controller;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.boundary.StripeBoundary;
import it.ispw.unilife.dao.ReservationDAO;
import it.ispw.unilife.dao.factory.DAOFactory;
import it.ispw.unilife.enums.LessonStatus;
import it.ispw.unilife.enums.PaymentStatus;
import it.ispw.unilife.enums.ReservationStatus;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.InvalidTokenException;
import it.ispw.unilife.exception.PaymentException;
import it.ispw.unilife.exception.UserNotFoundException;
import it.ispw.unilife.model.*;
import it.ispw.unilife.model.session.Session;
import it.ispw.unilife.model.session.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class BookTutor {

    private static final Logger logger = Logger.getLogger(BookTutor.class.getName());

    public List<String> getAvailableSubjects() {
        List<String> subjects = new ArrayList<>();
        try {
            List<Lesson> allLessons = DAOFactory.getDAOFactory().getLessonDAO().getAll();

            for (Lesson lesson : allLessons) {
                String subject = lesson.getSubject();

                if (subject != null && !subject.isEmpty() && !subjects.contains(subject)) {
                    subjects.add(subject);
                }
            }

            Collections.sort(subjects);

        } catch (DAOException | UserNotFoundException e) {
            logger.warning("Errore nel recupero delle materie: " + e.getMessage());
        }
        return subjects;
    }

    public List<LessonBean> filterTutor(FilterTutorBean filter) throws DAOException {
        List<Lesson> lessons = fetchAllLessons();
        List<Reservation> reservations = fetchAllReservations();

        List<LessonBean> lessonBeans = new ArrayList<>();

        for (Lesson lesson : lessons) {
            if (isLessonEligible(lesson, filter, reservations)) {
                lessonBeans.add(convertToLessonBean(lesson));
            }
        }

        return lessonBeans;
    }

    private List<Lesson> fetchAllLessons() throws DAOException {
        try {
            List<Lesson> lessons = DAOFactory.getDAOFactory().getLessonDAO().getAll();
            assert lessons != null;
            return lessons;
        } catch (UserNotFoundException e) {
            throw new DAOException("Errore nel recupero delle lezioni: " + e.getMessage());
        }
    }

    private List<Reservation> fetchAllReservations() throws DAOException {
        try {
            List<Reservation> reservations = DAOFactory.getDAOFactory().getReservationDAO().getAll();
            assert reservations != null;
            return reservations;
        } catch (UserNotFoundException e) {
            throw new DAOException("Errore nel recupero delle prenotazioni: " + e.getMessage());
        }
    }

    private boolean isLessonEligible(Lesson lesson, FilterTutorBean filter, List<Reservation> reservations) {
        if (!isLessonAvailableAndAccepted(lesson, filter)) {
            return false;
        }

        return !isLessonAlreadyReserved(lesson, reservations);
    }

    private boolean isLessonAvailableAndAccepted(Lesson lesson, FilterTutorBean filter) {
        return lesson.retrieveAvailability(filter.getStart(), filter.getEnd(), filter.getSubject(), filter.getAmount())
                && lesson.getStatus().equals(LessonStatus.ACCEPTED);
    }

    private boolean isLessonAlreadyReserved(Lesson lesson, List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            if (isReservationForLesson(reservation, lesson)) {
                return true;
            }
        }
        return false;
    }

    private boolean isReservationForLesson(Reservation reservation, Lesson lesson) {
        Lesson resLesson = reservation.checkLesson();

        if (resLesson == null) {
            return false;
        }

        return resLesson.getTutor().getUsername().equals(lesson.getTutor().getUsername())
                && resLesson.getStartTime().equals(lesson.getStartTime());
    }

    public ReservationBean startReservationProcedure(TokenBean tokenBean, LessonBean lessonBean) throws InvalidTokenException {
        User currentUser = getActiveUser(tokenBean);
        ReservationBean reservationBean = new ReservationBean();
        if (currentUser instanceof Student student) {
            reservationBean.setStudent(convertToStudentBean(student));
        }
        reservationBean.setLesson(lessonBean);
        reservationBean.setStatus(ReservationStatus.PENDING.toString());
        return reservationBean;
    }

    public void confirmReservationProcedure(TokenBean tokenBean, ReservationBean reservationBean) throws DAOException, InvalidTokenException, UserNotFoundException {
        User currentUser = getActiveUser(tokenBean);
        Lesson lesson = findLessonFromBean(reservationBean.getLesson());
        assert lesson != null;

        Reservation reservation = new Reservation((Student) currentUser, lesson, new Payment(lesson.calculateTotalPrice(), null, PaymentStatus.UNPAID), ReservationStatus.PENDING);
        ReservationDAO reservationDAO = DAOFactory.getDAOFactory().getReservationDAO();

        reservationDAO.insert(reservation);
        reservation.createReservation(tokenBean.getToken());
    }

    public void acceptReservationProcedure(TokenBean tokenBean, ReservationBean reservationBean) throws DAOException, InvalidTokenException, UserNotFoundException {
        Reservation reservation = findReservationFromBean(reservationBean);
        assert reservation != null;

        ReservationDAO reservationDAO = DAOFactory.getDAOFactory().getReservationDAO();
        reservation.setStatus(ReservationStatus.CONFIRMED, tokenBean.getToken());
        reservationDAO.update(reservation);
    }

    public void processPayment(TokenBean tokenBean, PaymentBean paymentBean, ReservationBean reservationBean) throws DAOException, UserNotFoundException {
        StripeBoundary boundary = new StripeBoundary();

        paymentBean = boundary.doPayment(paymentBean);

        if ("FAILED".equals(paymentBean.getStatus())) {
            throw new PaymentException("Pagamento rifiutato o fallito su Stripe.");
        }

        Reservation reservation = findReservationFromBean(reservationBean);

        ReservationDAO reservationDAO = DAOFactory.getDAOFactory().getReservationDAO();
        reservation.updatePayment(PaymentStatus.fromString(paymentBean.getStatus()), paymentBean.getStripe());
        reservation.completeStatus(tokenBean.getToken());

        reservationDAO.update(reservation);
    }

    public void abortReservationProcedure(TokenBean tokenBean, ReservationBean reservationBean) throws InvalidTokenException, DAOException, UserNotFoundException {
        Reservation reservation = findReservationFromBean(reservationBean);
        assert reservation != null;

        reservation.setStatus(ReservationStatus.CANCELLED, tokenBean.getToken());
        ReservationDAO reservationDAO = DAOFactory.getDAOFactory().getReservationDAO();
        reservationDAO.update(reservation);
    }

    public ReservationBean convertReservationToBean(Reservation reservation) {
        ReservationBean reservationBean = new ReservationBean();
        reservationBean.setStudent(convertToStudentBean(reservation.checkStudent()));
        reservationBean.setLesson(convertToLessonBean(reservation.checkLesson()));

        if (reservation.checkPayment() != null) {
            reservationBean.setPayment(convertToPaymentBean(reservation.checkPayment()));
        } else {
            reservationBean.setPayment(null);
        }

        reservationBean.setStatus(reservation.getStatus().toString());
        return reservationBean;
    }

    private Lesson findLessonFromBean(LessonBean lessonBean) throws DAOException, UserNotFoundException {
        List<Lesson> lessons = DAOFactory.getDAOFactory().getLessonDAO().getAll();
        for (Lesson lesson : lessons) {
            if (lesson.getTutor().getUsername().equals(lessonBean.getTutor().getUsername())
                    && lesson.getStartTime().equals(lessonBean.getStartTime())) {
                return lesson;
            }
        }
        throw new UserNotFoundException();
    }

    private Reservation findReservationFromBean(ReservationBean reservationBean) throws DAOException, UserNotFoundException {
        List<Reservation> reservations = DAOFactory.getDAOFactory().getReservationDAO().getAll();
        for (Reservation r : reservations) {
            if (r.checkStudent() != null && r.checkLesson() != null &&
                    r.checkStudent().getUsername().equals(reservationBean.getStudent().getUsername())
                    && r.checkTutor().getUsername().equals(reservationBean.getLesson().getTutor().getUsername())
                    && r.checkLesson().getStartTime().equals(reservationBean.getLesson().getStartTime())) {
                return r;
            }
        }
        throw new UserNotFoundException();
    }

    private PaymentBean convertToPaymentBean(Payment payment) {
        PaymentBean paymentBean = new PaymentBean();
        paymentBean.setAmount(payment.showCost());
        paymentBean.setStripe(payment.getStripe());
        paymentBean.setStatus(payment.getStatus().toString());
        return paymentBean;
    }

    private LessonBean convertToLessonBean(Lesson lesson) {
        LessonBean lessonBean = new LessonBean();
        lessonBean.setTutor(convertToTutorBean(lesson.getTutor()));
        lessonBean.setEndTime(lesson.getEndTime());
        lessonBean.setStartTime(lesson.getStartTime());
        lessonBean.setSubject(lesson.getSubject());
        lessonBean.setDurationInHours(lesson.getDurationInHours());
        lessonBean.setPrice(lesson.calculateTotalPrice());
        return lessonBean;
    }

    private TutorBean convertToTutorBean(Tutor tutor) {
        TutorBean tutorBean = new TutorBean();
        tutorBean.setName(tutor.getName());
        tutorBean.setSurname(tutor.getSurname());
        tutorBean.setRating(tutor.getRating());
        tutorBean.setUsername(tutor.getUsername());
        return tutorBean;
    }

    private StudentBean convertToStudentBean(Student student) {
        StudentBean studentBean = new StudentBean();
        studentBean.setName(student.getName());
        studentBean.setSurname(student.getSurname());
        studentBean.setUsername(student.getUsername());
        return studentBean;
    }

    private User getActiveUser(TokenBean tokenBean) throws InvalidTokenException {
        if (!SessionManager.getInstance().sessionIsValid(tokenBean.getToken())) {
            throw new InvalidTokenException("Token non valido");
        }
        Session session = SessionManager.getInstance().getSession(tokenBean.getToken());
        return session.getUser();
    }
}