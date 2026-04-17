package br.com.adoption.exception;

public class AdoptionRequestNotPendingException extends RuntimeException {
    public AdoptionRequestNotPendingException(String message) {
        super(message);
    }
}