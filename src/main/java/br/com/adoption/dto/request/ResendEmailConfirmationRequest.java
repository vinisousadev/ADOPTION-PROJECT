package br.com.adoption.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ResendEmailConfirmationRequest {

    @NotBlank
    @Email
    private String email;

    public ResendEmailConfirmationRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
