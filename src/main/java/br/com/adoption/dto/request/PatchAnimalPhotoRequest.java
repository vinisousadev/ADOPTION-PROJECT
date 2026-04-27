package br.com.adoption.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for partially updating an animal photo")
public class PatchAnimalPhotoRequest {

    @Schema(description = "Public URL of the image", example = "https://example.com/photos/mel-1.jpg")
    @Size(max = 255)
    private String photoUrl;

    @Schema(description = "Marks whether the photo is the main photo", example = "Y", allowableValues = {"Y", "N"})
    private Character isMain;

    public PatchAnimalPhotoRequest() {
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
}
