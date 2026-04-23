package br.com.adoption.dto.request;

import jakarta.validation.constraints.Size;

public class PatchAnimalPhotoRequest {

    @Size(max = 255)
    private String photoUrl;

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
