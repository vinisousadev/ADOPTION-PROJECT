package br.com.adoption.mapper;

import br.com.adoption.dto.request.CreateAnimalRequest;
import br.com.adoption.dto.response.AnimalResponse;
import br.com.adoption.entity.Animal;

import java.util.List;

public class AnimalMapper {

    public static Animal toEntity(CreateAnimalRequest request) {
        Animal animal = new Animal();
        animal.setAnimalName(request.getAnimalName());
        animal.setSpecies(request.getSpecies());
        animal.setBreed(request.getBreed());
        animal.setBirthDate(request.getBirthDate());
        animal.setAge(request.getAge());
        animal.setAnimalSize(request.getAnimalSize());
        animal.setSex(request.getSex());
        animal.setWeightKg(request.getWeightKg());
        animal.setVaccinated(request.getVaccinated());
        animal.setNeutered(request.getNeutered());
        animal.setDescription(request.getDescription());
        return animal;
    }

    public static AnimalResponse toResponse(Animal animal) {
        AnimalResponse response = new AnimalResponse();
        response.setId(animal.getId());
        response.setAnimalName(animal.getAnimalName());
        response.setSpecies(animal.getSpecies());
        response.setBreed(animal.getBreed());
        response.setBirthDate(animal.getBirthDate());
        response.setAge(animal.getAge());
        response.setAnimalSize(animal.getAnimalSize());
        response.setSex(animal.getSex());
        response.setWeightKg(animal.getWeightKg());
        response.setVaccinated(animal.getVaccinated());
        response.setNeutered(animal.getNeutered());
        response.setDescription(animal.getDescription());
        response.setStatus(animal.getStatus());
        response.setRegistrationDate(animal.getRegistrationDate());

        if (animal.getUser() != null) {
            response.setUserId(animal.getUser().getId());
        }

        return response;
    }

    public static List<AnimalResponse> toResponseList(List<Animal> animals) {
        return animals.stream()
                .map(AnimalMapper::toResponse)
                .toList();
    }
}
