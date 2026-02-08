package it.ispw.unilife.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrationException extends Exception {
    private static final Logger LOGGER = Logger.getLogger(RegistrationException.class.getName());
    public RegistrationException (String errStr){LOGGER.log(Level.SEVERE,errStr);}

    public RegistrationException() {

    }
}
