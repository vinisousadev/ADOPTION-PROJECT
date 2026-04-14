package br.com.adoption.service.impl;

import br.com.adoption.entity.AnimalPhoto;
import br.com.adoption.repository.AnimalPhotoRepository;
import br.com.adoption.service.AnimalPhotoService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalPhotoServiceImpl implements AnimalPhotoService {

    private final AnimalPhotoRepository animalPhotoRepository;

    public AnimalPhotoServiceImpl(AnimalPhotoRepository animalPhotoRepository) {
        this.animalPhotoRepository = animalPhotoRepository;
    }

    @Override
    public List<AnimalPhoto> getAllPhotos() {
        return animalPhotoRepository.findAll(Sort.by("id"));
    }

    @Override
    public AnimalPhoto save(AnimalPhoto animalPhoto) {
        return animalPhotoRepository.save(animalPhoto);
    }
}