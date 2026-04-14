package br.com.adoption.service.impl;

import br.com.adoption.entity.Animal;
import br.com.adoption.repository.AnimalRepository;
import br.com.adoption.service.AnimalService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;


import java.util.List;

@Service
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;

    public AnimalServiceImpl(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Override
    public List<Animal> getAvailableAnimals() {
        return animalRepository.findByStatus("AVAILABLE");
    }

    @Override
    public List<Animal> getAllAnimals() {
        return animalRepository.findAll(Sort.by("id"));
    }

    @Override
    public Animal save(Animal animal) {
        return animalRepository.save(animal);
    }
}