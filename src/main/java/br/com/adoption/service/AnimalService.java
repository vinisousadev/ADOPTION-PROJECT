package br.com.adoption.service;

import br.com.adoption.dto.request.CreateAnimalRequest;
import br.com.adoption.dto.response.AnimalResponse;

import java.util.List;

public interface AnimalService {

    List<AnimalResponse> getAvailableAnimals();
    List<AnimalResponse> getAllAnimals();
    AnimalResponse save(CreateAnimalRequest request);
}