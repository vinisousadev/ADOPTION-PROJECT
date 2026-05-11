package br.com.adoption.dto.response;

import java.time.OffsetDateTime;

public class AdoptionRequestMessageResponse {

    private Long id;
    private Long adoptionRequestId;
    private Long senderId;
    private String senderName;
    private String senderProfilePhotoUrl;
    private String message;
    private OffsetDateTime createdAt;
    private OffsetDateTime readAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdoptionRequestId() {
        return adoptionRequestId;
    }

    public void setAdoptionRequestId(Long adoptionRequestId) {
        this.adoptionRequestId = adoptionRequestId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderProfilePhotoUrl() {
        return senderProfilePhotoUrl;
    }

    public void setSenderProfilePhotoUrl(String senderProfilePhotoUrl) {
        this.senderProfilePhotoUrl = senderProfilePhotoUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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