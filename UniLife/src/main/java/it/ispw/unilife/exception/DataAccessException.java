package it.ispw.unilife.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DataAccessException extends Exception {
    public DataAccessException (String errStr){
        Logger.getLogger(DataAccessException.class.getName()).log(Level.SEVERE, errStr);
    }
}
