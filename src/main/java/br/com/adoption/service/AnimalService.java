package br.com.adoption.service;

import br.com.adoption.dto.request.CreateAnimalRequest;
import br.com.adoption.dto.request.PatchAnimalRequest;
import br.com.adoption.dto.request.UpdateAnimalRequest;
import br.com.adoption.dto.response.AnimalResponse;
import br.com.adoption.entity.AnimalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnimalService {

    List<AnimalResponse> getAvailableAnimals();
    Page<AnimalResponse> getAvailableAnimals(Pageable pageable,
                                             String species,
                                             String city,
                                             String animalSize,
                                             Character sex);
    List<AnimalResponse> getMyAnimals(String userEmail);
    Page<AnimalResponse> getMyAnimals(String userEmail, Pageable pageable);

    AnimalResponse getById(Long animalId, String userEmail);

    AnimalResponse save(CreateAnimalRequest request, String userEmail);

    AnimalResponse update(Long animalId, UpdateAnimalRequest request, String userEmail);
    AnimalResponse patch(Long animalId, PatchAnimalRequest request, String userEmail);

    AnimalResponse delete(Long animalId, String userEmail);

    List<AnimalResponse> getAllAnimals();
    Page<AnimalResponse> getAllAnimals(Pageable pageable,
                                       AnimalStatus status,
                                       String species,
                                       String city,
                                       String animalSize,
                                       Character sex);
}
