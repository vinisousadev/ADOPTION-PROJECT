package br.com.adoption.exception;

public class AnimalNotAvailableException extends RuntimeException {

    public AnimalNotAvailableException(String message) {
        super(message);
    }
}