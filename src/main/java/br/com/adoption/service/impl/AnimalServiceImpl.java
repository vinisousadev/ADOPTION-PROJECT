package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateAnimalRequest;
import br.com.adoption.dto.request.UpdateAnimalRequest;
import br.com.adoption.dto.response.AnimalResponse;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.User;
import br.com.adoption.entity.UserType;
import br.com.adoption.exception.OnlyOwnerCanManageAnimalException;
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
    public AnimalResponse getById(Long animalId, String userEmail) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        if ("AVAILABLE".equals(animal.getStatus())) {
            return AnimalMapper.toResponse(animal);
        }

        if (userEmail == null) {
            throw new ResourceNotFoundException("Animal not found");
        }

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = authenticatedUser.getUserType() == UserType.ADMIN;
        boolean isOwner = animal.getUser() != null
                && animal.getUser().getId().equals(authenticatedUser.getId());

        if (!isAdmin && !isOwner) {
            throw new ResourceNotFoundException("Animal not found");
        }

        return AnimalMapper.toResponse(animal);
    }

    @Override
    public AnimalResponse save(CreateAnimalRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Animal animal = AnimalMapper.toEntity(request);
        animal.setUser(user);
        animal.setStatus("AVAILABLE");

        Animal savedAnimal = animalRepository.save(animal);
        return AnimalMapper.toResponse(savedAnimal);
    }

    @Override
    public AnimalResponse update(Long animalId, UpdateAnimalRequest request, String userEmail) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateOwnerOrAdmin(animal, authenticatedUser);

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

        Animal updatedAnimal = animalRepository.save(animal);
        return AnimalMapper.toResponse(updatedAnimal);
    }

    @Override
    public AnimalResponse delete(Long animalId, String userEmail) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateOwnerOrAdmin(animal, authenticatedUser);

        animal.setStatus("REMOVED");

        Animal deletedAnimal = animalRepository.save(animal);
        return AnimalMapper.toResponse(deletedAnimal);
    }

    private void validateOwnerOrAdmin(Animal animal, User authenticatedUser) {
        boolean isAdmin = authenticatedUser.getUserType() == UserType.ADMIN;
        boolean isOwner = animal.getUser() != null
                && animal.getUser().getId().equals(authenticatedUser.getId());

        if (!isAdmin && !isOwner) {
            throw new OnlyOwnerCanManageAnimalException("Only the animal owner or admin can manage this animal");
        }
    }
}
