package br.com.adoption.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for creating an adoption request")
public class CreateAdoptionRequest {

    @Schema(description = "Optional message sent to the animal owner", example = "I have a large yard and experience with dogs")
    @Size(max = 500)
    private String message;

    @Schema(description = "Id of the animal being requested for adoption", example = "1")
    @NotNull
    @Positive
    private Long animalId;

    public CreateAdoptionRequest() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }
}
