package it.ispw.unilife.controller;

import it.ispw.unilife.bean.*;
import it.ispw.unilife.dao.UniversityDAO;
import it.ispw.unilife.dao.UserDAO;
import it.ispw.unilife.dao.factory.DAOFactory;
import it.ispw.unilife.enums.Role;
import it.ispw.unilife.exception.*;
import it.ispw.unilife.model.*;
import it.ispw.unilife.model.session.Session;
import it.ispw.unilife.model.session.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LoginController {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    private final ExternalLoginController externalLoginController;

    public LoginController() {
        this.externalLoginController = new ExternalLoginController();
    }

    public TokenBean login(UserBean userBean) throws LoginException {
        try {
            String username = userBean.getUserName();
            String password = userBean.getPassword();

            UserDAO userDao = DAOFactory.getDAOFactory().getUserDAO();
            List<User> res = userDao.getAll();

            for (User u : res) {
                if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                    String token = SessionManager.getInstance().createSession(u);
                    TokenBean tokenBean = new TokenBean();
                    tokenBean.setToken(token);

                    LOGGER.log(Level.INFO,"Login effettuato per: {0}", username);

                    return tokenBean;
                }
            }

            throw new LoginException("Credenziali non valide");

        } catch (DAOException e) {
            LOGGER.severe("Error: DAO ERROR");
        }

        return null;
    }

    public UserBean findUserRole(TokenBean bean){
        Session session = SessionManager.getInstance().getSession(bean.getToken());
        User user =  session.getUser();
        UserBean userBean = new UserBean();
        userBean.setRole(user.getRole().toString());
        return userBean;
    }

    public void invalidateToken(TokenBean bean){
        SessionManager.getInstance().invalidateSession(bean.getToken());
    }

    public boolean checkTokenValidity(TokenBean bean){
        return SessionManager.getInstance().sessionIsValid(bean.getToken());
    }

    public TokenBean externalLogin(String serviceName) throws ExternalAuthenticationException, UserNotFoundException {
        LOGGER.log(Level.INFO, "Login esterno richiesto per: {0}", serviceName);
        try {
            return externalLoginController.loginWithExternalService(serviceName);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalAuthenticationException(e.getMessage());
        } catch (DAOException e) {
            throw new ExternalAuthenticationException(e.getMessage());
        }
    }

    public TokenBean register(UserBean userBean) throws RegistrationException {
        try {
            UserDAO userDao = DAOFactory.getDAOFactory().getUserDAO();


            User user = convertBeanToModel(userBean);


            userDao.insert(user);

            LOGGER.info("Utente registrato: " + userBean.getUserName());

            String token = SessionManager.getInstance().createSession(user);
            TokenBean tokenBean = new TokenBean();
            tokenBean.setToken(token);
            return tokenBean;

        } catch (Exception e) {
            throw new RegistrationException("Errore durante la registrazione: " + e.getMessage());
        }
    }

    public List<UniversityBean> findAvailableUniversities() throws DAOException {
        UniversityDAO  universityDao = DAOFactory.getDAOFactory().getUniversityDAO();
        List<UniversityBean> results = new ArrayList<>();

        try {
            List<University> res = universityDao.getAll();
            for (University un : res) {
                UniversityBean bean = new UniversityBean();
                bean.setName(un.getName());
                results.add(bean);
            }
        } catch (DAOException | UserNotFoundException e) {
            LOGGER.severe(e.getMessage());
        }
        return results;
    }

    public UserBean getProfile(TokenBean bean){
        User user = SessionManager.getInstance().getSession(bean.getToken()).getUser();
        return convertToUserBean(user);
    }

    private UserBean convertToUserBean(User user){
        UserBean userBean = new UserBean();
        userBean.setRole(user.getRole().toString());
        userBean.setName(user.getName());
        userBean.setPassword(user.getPassword());
        userBean.setUserName(user.getUsername());
        userBean.setSurname(user.getSurname());
        return userBean;
    }


    private User convertBeanToModel(UserBean bean) throws UserNotFoundException, DAOException {

        String username = bean.getUserName();
        String name = bean.getName();
        String surname = bean.getSurname();
        String password = bean.getPassword();

        Role roleEnum;
        try {
            roleEnum = Role.fromString(bean.getRole());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new UserNotFoundException();
        }

        switch (roleEnum) {
            case TUTOR:
                return new Tutor(username, name, surname, password);
            case STUDENT:
                return new Student(username, name, surname, password);
            case UNIVERSITY_EMPLOYEE:

                String university = bean.getUniversity();

                UniversityDAO  universityDao = DAOFactory.getDAOFactory().getUniversityDAO();
                try {
                    List<University> res = universityDao.getAll();
                    for (University un : res) {
                        if(Objects.equals(un.getName(), university)) {
                            return new UniversityEmployee(username, name, surname, password, un);
                        }

                    }
                } catch (DAOException e) {
                    throw new DAOException(e.getMessage());
                }
                break;

            default:
                throw new UserNotFoundException();
        }
        return null;
    }
}
