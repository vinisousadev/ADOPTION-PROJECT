package br.com.adoption.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateAnimalPhotoRequest {

    @NotBlank
    @Size(max = 255)
    private String photoUrl;

    @NotNull
    private Character isMain;

    public UpdateAnimalPhotoRequest() {
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
