package br.com.adoption.exception;

public class OnlyOwnerCanManageUserException extends RuntimeException {
    public OnlyOwnerCanManageUserException(String message) {
        super(message);
    }
}
