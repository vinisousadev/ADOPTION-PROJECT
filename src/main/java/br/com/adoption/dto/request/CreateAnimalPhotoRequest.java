package br.com.adoption.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for creating an animal photo")
public class CreateAnimalPhotoRequest {

    @Schema(description = "Public URL of the image", example = "https://example.com/photos/mel-1.jpg")
    @NotBlank
    @Size(max = 255)
    private String photoUrl;

    @Schema(description = "Marks whether the photo is the main photo", example = "Y", allowableValues = {"Y", "N"})
    @NotNull
    private Character isMain;

    @Schema(description = "Id of the related animal", example = "1")
    @NotNull
    @Positive
    private Long animalId;

    public CreateAnimalPhotoRequest() {
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Character getIsMain() {
        return isMain;
    }

    public void setIsMain(Character isMain) {
        this.isMain = isMain;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }
}
