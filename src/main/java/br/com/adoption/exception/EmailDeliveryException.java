package br.com.adoption.exception;

public class EmailDeliveryException extends RuntimeException {
    public EmailDeliveryException(String message) {
        super(message);
    }
}
