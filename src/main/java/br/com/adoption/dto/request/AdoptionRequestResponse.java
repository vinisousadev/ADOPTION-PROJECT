package br.com.adoption.dto.request;

import br.com.adoption.entity.AdoptionRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Adoption request response payload")
public class AdoptionRequestResponse {

    @Schema(description = "Unique adoption request identifier", example = "1")
    private Long id;
    @Schema(description = "Optional message sent by the requester", example = "I have a large yard and experience with dogs")
    private String message;
    @Schema(description = "Current request status", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED", "CANCELLED"})
    private AdoptionRequestStatus status;
    @Schema(description = "Date and time when the request was created", example = "2026-04-27T10:15:30")
    private OffsetDateTime requestDate;
    @Schema(description = "Date and time when the request was answered", example = "2026-04-28T09:00:00")
    private OffsetDateTime responseDate;
    @Schema(description = "Requested animal id", example = "1")
    private Long animalId;
    @Schema(description = "Requested animal name", example = "Mel")
    private String animalName;
    @Schema(description = "Requester user id", example = "2")
    private Long userId;
    @Schema(description = "Requester display name", example = "Joao Silva")
    private String requesterName;
    @Schema(description = "Animal owner user id", example = "1")
    private Long ownerId;
    @Schema(description = "Animal owner display name", example = "Ana Souza")
    private String ownerName;
    @Schema(description = "Animal owner city", example = "Joao Pessoa")
    private String ownerCity;
    @Schema(description = "Animal owner state", example = "PB")
    private String ownerState;

    public AdoptionRequestResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerCity() {
        return ownerCity;
    }

    public void setOwnerCity(String ownerCity) {
        this.ownerCity = ownerCity;
    }

    public String getOwnerState() {
        return ownerState;
    }

    public void setOwnerState(String ownerState) {
        this.ownerState = ownerState;
    }
}
