package it.ispw.unilife.exception;

public class PaymentException extends DAOException {
    public PaymentException(String message) {
        super("Il pagamento non trovata: " + message);
    }
}
