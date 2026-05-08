package br.com.adoption.dto.response;

import br.com.adoption.entity.UserType;
import br.com.adoption.entity.UserRoleLabel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

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
    @Schema(description = "Public URL of the user profile photo", example = "https://example.com/profile.jpg")
    private String profilePhotoUrl;
    @Schema(description = "Public role label displayed in profile and feed", example = "PROTETOR", allowableValues = {"ONG", "PROTETOR"})
    private UserRoleLabel roleLabel;
    @Schema(description = "Date and time when the user was registered", example = "2026-04-27T10:15:30")
    private OffsetDateTime registrationDate;
    @Schema(description = "Access profile of the user", example = "COMMON", allowableValues = {"COMMON", "ADMIN"})
    private UserType userType;
    @Schema(description = "Whether the user email has been confirmed", example = "true")
    private boolean emailVerified;

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

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public UserRoleLabel getRoleLabel() {
        return roleLabel;
    }

    public void setRoleLabel(UserRoleLabel roleLabel) {
        this.roleLabel = roleLabel;
    }

    public OffsetDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(OffsetDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
