package br.com.adoption.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "NOTIFICATION")
@SequenceGenerator(
        name = "notification_seq",
        sequenceName = "SEQ_NOTIFICATION",
        allocationSize = 1
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    @Column(name = "ID_NOTIFICATION")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "RECIPIENT_USER_ID", nullable = false)
    private User recipient;

    @Column(name = "TITLE", nullable = false, length = 120)
    private String title;

    @Column(name = "MESSAGE", nullable = false, length = 500)
    private String message;

    @Column(name = "TYPE", nullable = false, length = 60)
    private String type;

    @Column(name = "RELATED_ENTITY_TYPE", length = 60)
    private String relatedEntityType;

    @Column(name = "RELATED_ENTITY_ID")
    private Long relatedEntityId;

    @Column(name = "ACTION_URL", length = 255)
    private String actionUrl;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "READ_AT")
    private OffsetDateTime readAt;

    public Long getId() {
        return id;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(OffsetDateTime readAt) {
        this.readAt = readAt;
    }
}
