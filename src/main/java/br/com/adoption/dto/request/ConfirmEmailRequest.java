package br.com.adoption.dto.request;
import jakarta.validation.constraints.NotBlank;

public class ConfirmEmailRequest {

    @NotBlank
    private String token;

    public ConfirmEmailRequest() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
