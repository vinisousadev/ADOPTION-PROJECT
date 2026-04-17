package br.com.adoption.exception;

public class OnlyOwnerCanManageAdoptionRequestException extends RuntimeException {
    public OnlyOwnerCanManageAdoptionRequestException(String message) {
        super(message);
    }
}