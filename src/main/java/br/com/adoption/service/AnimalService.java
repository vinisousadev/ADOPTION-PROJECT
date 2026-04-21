package br.com.adoption.service;

import br.com.adoption.dto.request.CreateAnimalRequest;
import br.com.adoption.dto.request.UpdateAnimalRequest;
import br.com.adoption.dto.response.AnimalResponse;

import java.util.List;

public interface AnimalService {

    List<AnimalResponse> getAvailableAnimals();

    AnimalResponse getById(Long animalId, String userEmail);

    AnimalResponse save(CreateAnimalRequest request, String userEmail);

    AnimalResponse update(Long animalId, UpdateAnimalRequest request, String userEmail);

    AnimalResponse delete(Long animalId, String userEmail);

    List<AnimalResponse> getAllAnimals();
}
