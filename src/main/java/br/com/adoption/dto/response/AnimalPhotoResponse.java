package br.com.adoption.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Animal photo response payload")
public class AnimalPhotoResponse {

    @Schema(description = "Unique photo identifier", example = "1")
    private Long id;
    @Schema(description = "Public URL of the image", example = "https://example.com/photos/mel-1.jpg")
    private String photoUrl;
    @Schema(description = "Marks whether the photo is the main photo", example = "Y", allowableValues = {"Y", "N"})
    private Character isMain;
    @Schema(description = "Related animal id", example = "1")
    private Long animalId;

    public AnimalPhotoResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
