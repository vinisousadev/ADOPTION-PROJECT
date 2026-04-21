package br.com.adoption.exception;

public class OnlyOwnerCanManageAnimalException extends RuntimeException {
    public OnlyOwnerCanManageAnimalException(String message) {
        super(message);
    }
}
