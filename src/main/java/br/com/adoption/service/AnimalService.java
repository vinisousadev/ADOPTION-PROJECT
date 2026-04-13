package br.com.adoption.service;

import br.com.adoption.entity.Animal;

import java.util.List;

public interface AnimalService {

    List<Animal> getAvailableAnimals();
    List<Animal> getAllAnimals();
    Animal save(Animal animal);
}
