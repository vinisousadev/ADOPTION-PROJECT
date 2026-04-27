package br.com.adoption.dto.response;

import br.com.adoption.entity.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response payload")
public class LoginResponse {

    @Schema(description = "Authenticated user id", example = "1")
    private Long userId;
    @Schema(description = "Authenticated user name", example = "Ana Souza")
    private String name;
    @Schema(description = "Authenticated user email", example = "ana@email.com")
    private String email;
    @Schema(description = "Access profile of the authenticated user", example = "COMMON", allowableValues = {"COMMON", "ADMIN"})
    private UserType userType;
    @Schema(description = "Human-readable login result", example = "Login successful")
    private String message;
    @Schema(description = "JWT bearer token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    public LoginResponse() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
