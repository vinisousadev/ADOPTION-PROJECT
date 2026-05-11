package br.com.adoption.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ADOPTION_REQUEST_MESSAGE")
@SequenceGenerator(
        name = "SEQ_ADOPTION_REQUEST_MESSAGE",
        sequenceName = "SEQ_ADOPTION_REQUEST_MESSAGE",
        allocationSize = 1
)
public class AdoptionRequestMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ADOPTION_REQUEST_MESSAGE")
    @Column(name = "ID_MESSAGE")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_ADOPTION_REQUEST_ID", nullable = false)
    private AdoptionRequest adoptionRequest;

    @ManyToOne
    @JoinColumn(name = "FK_SENDER_USER_ID", nullable = false)
    private User sender;

    @Column(name = "MESSAGE", nullable = false, length = 1000)
    private String message;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "READ_AT")
    private OffsetDateTime readAt;

    public Long getId() { return id; }

    public AdoptionRequest getAdoptionRequest() { return adoptionRequest; }
    public void setAdoptionRequest(AdoptionRequest adoptionRequest) { this.adoptionRequest = adoptionRequest; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getReadAt() { return readAt; }
    public void setReadAt(OffsetDateTime readAt) { this.readAt = readAt; }
}
