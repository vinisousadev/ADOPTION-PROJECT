package br.com.adoption.dto.response;

import br.com.adoption.entity.AdoptionRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Adoption request response payload")
public class AdoptionRequestResponse {

    @Schema(description = "Unique adoption request identifier", example = "1")
    private Long id;
    @Schema(description = "Optional message sent by the requester", example = "I have a large yard and experience with dogs")
    private String message;
    @Schema(description = "Current request status", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED", "CANCELLED"})
    private AdoptionRequestStatus status;
    @Schema(description = "Date and time when the request was created", example = "2026-04-27T10:15:30")
    private LocalDateTime requestDate;
    @Schema(description = "Date and time when the request was answered", example = "2026-04-28T09:00:00")
    private LocalDateTime responseDate;
    @Schema(description = "Requested animal id", example = "1")
    private Long animalId;
    @Schema(description = "Requester user id", example = "2")
    private Long userId;

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

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
