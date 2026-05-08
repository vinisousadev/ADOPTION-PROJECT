package br.com.adoption.exception;

public class StorageUploadException extends RuntimeException {
    public StorageUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
