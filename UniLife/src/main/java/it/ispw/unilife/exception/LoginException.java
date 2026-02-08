package it.ispw.unilife.exception;

public class LoginException extends Exception {
    public LoginException(String errStr) {
        super(errStr);
    }

    public LoginException() {
    }
}
