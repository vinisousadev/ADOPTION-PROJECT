package br.com.adoption.mapper;

import br.com.adoption.dto.request.CreateAnimalPhotoRequest;
import br.com.adoption.dto.response.AnimalPhotoResponse;
import br.com.adoption.entity.AnimalPhoto;

import java.util.List;

public class AnimalPhotoMapper {

    public static AnimalPhoto toEntity(CreateAnimalPhotoRequest request) {
        AnimalPhoto animalPhoto = new AnimalPhoto();
        animalPhoto.setPhotoUrl(request.getPhotoUrl());
        animalPhoto.setIsMain(request.getIsMain());
        return animalPhoto;
    }

    public static AnimalPhotoResponse toResponse(AnimalPhoto animalPhoto) {
        AnimalPhotoResponse response = new AnimalPhotoResponse();
        response.setId(animalPhoto.getId());
        response.setPhotoUrl(animalPhoto.getPhotoUrl());
        response.setIsMain(animalPhoto.getIsMain());

        if (animalPhoto.getAnimal() != null) {
            response.setAnimalId(animalPhoto.getAnimal().getId());
        }

        return response;
    }

    public static List<AnimalPhotoResponse> toResponseList(List<AnimalPhoto> animalPhotos) {
        return animalPhotos.stream()
                .map(AnimalPhotoMapper::toResponse)
                .toList();
    }
}