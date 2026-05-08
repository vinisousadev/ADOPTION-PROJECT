package br.com.adoption.exception;

public class DuplicateUserEmailException extends RuntimeException {
    public DuplicateUserEmailException(String message) {
        super(message);
    }
}
