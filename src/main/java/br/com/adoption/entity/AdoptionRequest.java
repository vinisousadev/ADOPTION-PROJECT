package br.com.adoption.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ADOPTION_REQUEST")
@SequenceGenerator(
        name = "SEQ_ADOPTION_REQUEST",
        sequenceName = "SEQ_REQUEST",
        allocationSize = 1
)
public class AdoptionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ADOPTION_REQUEST")
    @Column(name = "ID_REQUEST")
    private Long id;

    @Column(name = "MESSAGE", length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private AdoptionRequestStatus status;

    @Column(name = "REQUEST_DATE", nullable = false)
    private OffsetDateTime requestDate;

    @Column(name = "RESPONSE_DATE")
    private OffsetDateTime responseDate;

    @ManyToOne
    @JoinColumn(name = "FK_ANIMAL_ID_ANIMAL", nullable = false)
    private Animal animal;

    @ManyToOne
    @JoinColumn(name = "FK_USERS_ID_USER", nullable = false)
    private User user;

    public AdoptionRequest() {
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AdoptionRequestStatus getStatus() {
        return status;
    }

    public void setStatus(AdoptionRequestStatus status) {
        this.status = status;
    }

    public OffsetDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(OffsetDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public OffsetDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(OffsetDateTime responseDate) {
        this.responseDate = responseDate;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}