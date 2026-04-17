package br.com.adoption.exception;

    public class OwnerCannotAdoptOwnAnimalException extends RuntimeException {
        public OwnerCannotAdoptOwnAnimalException(String message) {
            super(message);
        }
    }
