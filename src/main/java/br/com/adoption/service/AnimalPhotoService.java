package br.com.adoption.service;

import br.com.adoption.dto.request.CreateAnimalPhotoRequest;
import br.com.adoption.dto.response.AnimalPhotoResponse;

import java.util.List;

public interface AnimalPhotoService {
    List<AnimalPhotoResponse> getAllPhotos();
    AnimalPhotoResponse save(CreateAnimalPhotoRequest request);
}