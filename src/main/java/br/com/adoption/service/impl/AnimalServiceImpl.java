package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateAnimalRequest;
import br.com.adoption.dto.response.AnimalResponse;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.User;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.mapper.AnimalMapper;
import br.com.adoption.repository.AnimalRepository;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.AnimalService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    public List<AnimalResponse> getAvailableAnimals() {
        return AnimalMapper.toResponseList(animalRepository.findByStatus("AVAILABLE"));
    }

    @Override
    public List<AnimalResponse> getAllAnimals() {
        return AnimalMapper.toResponseList(animalRepository.findAll(Sort.by("id")));
    }

    @Override
    public AnimalResponse save(CreateAnimalRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Animal animal = AnimalMapper.toEntity(request);
        animal.setUser(user);
        animal.setStatus("AVAILABLE");

        Animal savedAnimal = animalRepository.save(animal);
        return AnimalMapper.toResponse(savedAnimal);
    }
}