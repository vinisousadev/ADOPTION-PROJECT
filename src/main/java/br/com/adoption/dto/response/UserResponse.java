package br.com.adoption.dto.response;

import br.com.adoption.entity.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "User response payload")
public class UserResponse {

    @Schema(description = "Unique user identifier", example = "1")
    private Long id;
    @Schema(description = "Full name of the user", example = "Ana Souza")
    private String name;
    @Schema(description = "CPF of the user", example = "12345678900")
    private String cpf;
    @Schema(description = "Phone number", example = "83999999999")
    private String phone;
    @Schema(description = "User email", example = "ana@email.com")
    private String email;
    @Schema(description = "City where the user lives", example = "Joao Pessoa")
    private String city;
    @Schema(description = "Brazilian state abbreviation", example = "PB")
    private String state;
    @Schema(description = "Date and time when the user was registered", example = "2026-04-27T10:15:30")
    private LocalDateTime registrationDate;
    @Schema(description = "Access profile of the user", example = "COMMON", allowableValues = {"COMMON", "ADMIN"})
    private UserType userType;

    public UserResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
