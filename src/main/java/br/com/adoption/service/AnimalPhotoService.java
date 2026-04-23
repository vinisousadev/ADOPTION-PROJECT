package br.com.adoption.service;

import br.com.adoption.dto.request.CreateAnimalPhotoRequest;
import br.com.adoption.dto.request.PatchAnimalPhotoRequest;
import br.com.adoption.dto.request.UpdateAnimalPhotoRequest;
import br.com.adoption.dto.response.AnimalPhotoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnimalPhotoService {
    List<AnimalPhotoResponse> getAllPhotos();
    Page<AnimalPhotoResponse> getAllPhotos(Pageable pageable);
    Page<AnimalPhotoResponse> getAllPhotos(Long animalId, Pageable pageable);
    AnimalPhotoResponse getById(Long photoId);
    AnimalPhotoResponse save(CreateAnimalPhotoRequest request, String userEmail);
    AnimalPhotoResponse update(Long photoId, UpdateAnimalPhotoRequest request, String userEmail);
    AnimalPhotoResponse patch(Long photoId, PatchAnimalPhotoRequest request, String userEmail);
    AnimalPhotoResponse delete(Long photoId, String userEmail);
}
