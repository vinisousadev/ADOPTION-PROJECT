package br.com.adoption.service.impl;

import br.com.adoption.entity.Animal;
import br.com.adoption.repository.AnimalRepository;
import br.com.adoption.service.AnimalService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import br.com.adoption.entity.User;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.repository.UserRepository;


import java.util.List;

@Service
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;

    public AnimalServiceImpl(AnimalRepository animalRepository,
                             UserRepository userRepository) {
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
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
        User user = userRepository.findById(animal.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        animal.setUser(user);
        animal.setStatus("AVAILABLE");

        return animalRepository.save(animal);
    }
}