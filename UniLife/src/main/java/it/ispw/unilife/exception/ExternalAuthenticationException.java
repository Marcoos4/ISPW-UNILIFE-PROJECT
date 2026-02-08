package it.ispw.unilife.exception;

public class ExternalAuthenticationException extends RuntimeException {
    public ExternalAuthenticationException(String message) {
        super(message);
    }
}
