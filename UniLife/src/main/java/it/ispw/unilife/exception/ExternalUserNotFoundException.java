package it.ispw.unilife.exception;

import it.ispw.unilife.bean.UserBean;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExternalUserNotFoundException extends UserNotFoundException implements Serializable {

    private final transient UserBean userBean;

    public ExternalUserNotFoundException(String message, UserBean userBean) {
        // 3. Passa il messaggio alla superclasse (Exception) invece di loggarlo nel costruttore.
        // È chi "cattura" l'eccezione che dovrebbe decidere se loggarla.
        super();
        Logger.getLogger(ExternalUserNotFoundException.class.getName()).log(Level.WARNING, message);
        // 4. Assegnazione corretta all'istanza (non più statica)
        this.userBean = userBean;
    }

    public UserBean getUserBean() {
        return userBean;
    }
}