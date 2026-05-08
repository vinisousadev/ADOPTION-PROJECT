package br.com.adoption.dto.response;

public class EmailConfirmationResponse {

    private String message;
    private boolean emailVerified;

    public EmailConfirmationResponse() {
    }

    public EmailConfirmationResponse(String message, boolean emailVerified) {
        this.message = message;
        this.emailVerified = emailVerified;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
