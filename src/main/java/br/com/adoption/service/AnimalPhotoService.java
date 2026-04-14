package br.com.adoption.service;

import br.com.adoption.entity.AnimalPhoto;

import java.util.List;

public interface AnimalPhotoService {
    List<AnimalPhoto> getAllPhotos();
    AnimalPhoto save(AnimalPhoto animalPhoto);
}