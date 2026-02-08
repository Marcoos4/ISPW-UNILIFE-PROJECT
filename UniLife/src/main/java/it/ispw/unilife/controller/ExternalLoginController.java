package it.ispw.unilife.controller;

import it.ispw.unilife.bean.ExternalAuthResultBean;
import it.ispw.unilife.bean.TokenBean;
import it.ispw.unilife.bean.UserBean;
import it.ispw.unilife.boundary.ExternalLogin;
import it.ispw.unilife.boundary.GithubAuthAdapter;
import it.ispw.unilife.boundary.GoogleAuthAdapter;
import it.ispw.unilife.dao.UserDAO;
import it.ispw.unilife.dao.factory.DAOFactory;
import it.ispw.unilife.exception.DAOException;
import it.ispw.unilife.exception.ExternalAuthenticationException;
import it.ispw.unilife.exception.ExternalUserNotFoundException;
import it.ispw.unilife.exception.UserNotFoundException; 
import it.ispw.unilife.model.User;
import it.ispw.unilife.model.session.SessionManager;

import java.util.List;
import java.util.logging.Logger;

public class ExternalLoginController {

    private static final Logger LOGGER = Logger.getLogger(ExternalLoginController.class.getName());
    private UserDAO userDAO;

    public ExternalLoginController() {
        try {
            this.userDAO = DAOFactory.getDAOFactory().getUserDAO();
        } catch (DAOException e) {
            LOGGER.severe(e.getMessage());
        }
    }


    public TokenBean loginWithExternalService(String serviceName) throws ExternalAuthenticationException, UserNotFoundException, DAOException, InterruptedException {
        LOGGER.info("Avvio login esterno");

        ExternalLogin adapter = createAdapter(serviceName);
        ExternalAuthResultBean authResult = adapter.authenticate();

        if (!authResult.isSuccess()) {
            throw new ExternalAuthenticationException(authResult.getErrorMessage());
        }

        UserBean userBean = authResult.getUserBean();


        User user = findUser(userBean);

        String token = SessionManager.getInstance().createSession(user);
        TokenBean tokenBean = new TokenBean();
        tokenBean.setToken(token);

        LOGGER.info("Login completato per: " + user.getUsername());
        return tokenBean;
    }

    private ExternalLogin createAdapter(String serviceName) throws ExternalAuthenticationException {
        if ("Google".equalsIgnoreCase(serviceName)) {
            return new GoogleAuthAdapter();
        } else if ("GitHub".equalsIgnoreCase(serviceName)) {
            return new GithubAuthAdapter();
        } else {
            throw new ExternalAuthenticationException("Servizio non supportato: " + serviceName);
        }
    }


    private User findUser(UserBean userBean) throws UserNotFoundException, DAOException {
        try {
            List<User> users = userDAO.getAll();

            for (User user : users) {
                if (userBean.getUserName().equalsIgnoreCase(user.getUsername())) {
                    return user;
                }
            }


            throw new ExternalUserNotFoundException("Utente esterno non registrato", userBean);

        } catch (DAOException e) {
            throw new DAOException("Errore Database durante login esterno");
        }
    }
}
