package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateAnimalPhotoRequest;
import br.com.adoption.dto.response.AnimalPhotoResponse;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.AnimalPhoto;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.mapper.AnimalPhotoMapper;
import br.com.adoption.repository.AnimalPhotoRepository;
import br.com.adoption.repository.AnimalRepository;
import br.com.adoption.service.AnimalPhotoService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalPhotoServiceImpl implements AnimalPhotoService {

    private final AnimalPhotoRepository animalPhotoRepository;
    private final AnimalRepository animalRepository;

    public AnimalPhotoServiceImpl(AnimalPhotoRepository animalPhotoRepository,
                                  AnimalRepository animalRepository) {
        this.animalPhotoRepository = animalPhotoRepository;
        this.animalRepository = animalRepository;
    }

    @Override
    public List<AnimalPhotoResponse> getAllPhotos() {
        return AnimalPhotoMapper.toResponseList(animalPhotoRepository.findAll(Sort.by("id")));
    }

    @Override
    public AnimalPhotoResponse save(CreateAnimalPhotoRequest request) {
        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        AnimalPhoto animalPhoto = AnimalPhotoMapper.toEntity(request);
        animalPhoto.setAnimal(animal);

        AnimalPhoto savedPhoto = animalPhotoRepository.save(animalPhoto);
        return AnimalPhotoMapper.toResponse(savedPhoto);
    }
}