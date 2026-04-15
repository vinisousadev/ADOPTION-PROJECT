package br.com.adoption.exception;

public class DuplicateAdoptionRequestException extends RuntimeException {

    public DuplicateAdoptionRequestException(String message) {
        super(message);
    }
}