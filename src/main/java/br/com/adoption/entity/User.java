package br.com.adoption.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@Entity
@Table(name = "USERS")
@SequenceGenerator(
        name = "user_seq",
        sequenceName = "SEQ_USER",
        allocationSize = 1
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @Column(name = "ID_USER")
    private Long id;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "CPF", length = 14)
    private String cpf;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "EMAIL", nullable = false, length = 120)
    private String email;

    @Column(name = "CITY", length = 100)
    private String city;

    @Column(name = "STATE", length = 2)
    private String state;

    @Column(name = "PROFILE_PHOTO_URL", length = 500)
    private String profilePhotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE_LABEL", nullable = false, length = 30)
    private UserRoleLabel roleLabel;

    @Column(name = "REGISTRATION_DATE", nullable = false)
    private OffsetDateTime registrationDate;

    @Column(name = "EMAIL_VERIFIED", nullable = false)
    private boolean emailVerified = true;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "EMAIL_VERIFICATION_TOKEN_HASH", length = 64)
    private String emailVerificationTokenHash;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "EMAIL_VERIFICATION_EXPIRES_AT")
    private OffsetDateTime emailVerificationExpiresAt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_TYPE", nullable = false, length = 30)
    private UserType userType;

    public User() {
    }

    public Long getId() {
        return id;
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

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getEmailVerificationTokenHash() {
        return emailVerificationTokenHash;
    }

    public void setEmailVerificationTokenHash(String emailVerificationTokenHash) {
        this.emailVerificationTokenHash = emailVerificationTokenHash;
    }

    public OffsetDateTime getEmailVerificationExpiresAt() {
        return emailVerificationExpiresAt;
    }

    public void setEmailVerificationExpiresAt(OffsetDateTime emailVerificationExpiresAt) {
        this.emailVerificationExpiresAt = emailVerificationExpiresAt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
