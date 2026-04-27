package br.com.adoption.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for creating a new user")
public class CreateUserRequest {

    @Schema(description = "Full name of the user", example = "Ana Souza")
    @NotBlank
    @Size(max = 100)
    private String name;

    @Schema(description = "CPF of the user", example = "12345678900")
    @NotBlank
    @Size(min = 11, max = 14)
    private String cpf;

    @Schema(description = "Phone number", example = "83999999999")
    @Size(max = 20)
    private String phone;

    @Schema(description = "Email used for login", example = "ana@email.com")
    @NotBlank
    @Email
    @Size(max = 120)
    private String email;

    @Schema(description = "City where the user lives", example = "Joao Pessoa")
    @Size(max = 100)
    private String city;

    @Schema(description = "Brazilian state abbreviation", example = "PB")
    @Size(min = 2, max = 2)
    private String state;

    @Schema(description = "Plain password sent for account creation", example = "123456")
    @NotBlank
    @Size(max = 255)
    private String passwordHash;

    public CreateUserRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
