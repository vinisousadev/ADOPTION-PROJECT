package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateAnimalRequest;
import br.com.adoption.dto.request.PatchAnimalRequest;
import br.com.adoption.dto.request.UpdateAnimalRequest;
import br.com.adoption.dto.response.AnimalResponse;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.AnimalStatus;
import br.com.adoption.entity.User;
import br.com.adoption.entity.UserType;
import br.com.adoption.exception.OnlyOwnerCanManageAnimalException;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.mapper.AnimalMapper;
import br.com.adoption.repository.AnimalRepository;
import br.com.adoption.repository.UserRepository;
import jakarta.persistence.criteria.JoinType;
import br.com.adoption.service.AnimalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        return AnimalMapper.toResponseList(animalRepository.findByStatus(AnimalStatus.AVAILABLE));
    }

    @Override
    public Page<AnimalResponse> getAvailableAnimals(Pageable pageable,
                                                    String species,
                                                    String city,
                                                    String animalSize,
                                                    Character sex) {
        return animalRepository.findAll(
                buildAnimalFilterSpecification(AnimalStatus.AVAILABLE, species, city, animalSize, sex),
                pageable
        ).map(AnimalMapper::toResponse);
    }

    @Override
    public List<AnimalResponse> getMyAnimals(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return AnimalMapper.toResponseList(animalRepository.findByUser_IdOrderById(user.getId()));
    }

    @Override
    public Page<AnimalResponse> getMyAnimals(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return animalRepository.findByUser_Id(user.getId(), pageable).map(AnimalMapper::toResponse);
    }

    @Override
    public List<AnimalResponse> getAllAnimals() {
        return AnimalMapper.toResponseList(animalRepository.findAll(Sort.by("id")));
    }

    @Override
    public Page<AnimalResponse> getAllAnimals(Pageable pageable,
                                              AnimalStatus status,
                                              String species,
                                              String city,
                                              String animalSize,
                                              Character sex) {
        return animalRepository.findAll(
                buildAnimalFilterSpecification(status, species, city, animalSize, sex),
                pageable
        ).map(AnimalMapper::toResponse);
    }

    @Override
    public AnimalResponse getById(Long animalId, String userEmail) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        if (animal.getStatus() == AnimalStatus.AVAILABLE) {
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
        animal.setStatus(AnimalStatus.AVAILABLE);
        animal.setRegistrationDate(LocalDateTime.now());

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
    public AnimalResponse patch(Long animalId, PatchAnimalRequest request, String userEmail) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateOwnerOrAdmin(animal, authenticatedUser);

        if (request.getAnimalName() != null) {
            animal.setAnimalName(request.getAnimalName());
        }
        if (request.getSpecies() != null) {
            animal.setSpecies(request.getSpecies());
        }
        if (request.getBreed() != null) {
            animal.setBreed(request.getBreed());
        }
        if (request.getBirthDate() != null) {
            animal.setBirthDate(request.getBirthDate());
        }
        if (request.getAge() != null) {
            animal.setAge(request.getAge());
        }
        if (request.getAnimalSize() != null) {
            animal.setAnimalSize(request.getAnimalSize());
        }
        if (request.getSex() != null) {
            animal.setSex(request.getSex());
        }
        if (request.getWeightKg() != null) {
            animal.setWeightKg(request.getWeightKg());
        }
        if (request.getVaccinated() != null) {
            animal.setVaccinated(request.getVaccinated());
        }
        if (request.getNeutered() != null) {
            animal.setNeutered(request.getNeutered());
        }
        if (request.getDescription() != null) {
            animal.setDescription(request.getDescription());
        }

        Animal patchedAnimal = animalRepository.save(animal);
        return AnimalMapper.toResponse(patchedAnimal);
    }

    @Override
    public AnimalResponse delete(Long animalId, String userEmail) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateOwnerOrAdmin(animal, authenticatedUser);

        animal.setStatus(AnimalStatus.REMOVED);

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

    private Specification<Animal> buildAnimalFilterSpecification(AnimalStatus status,
                                                                 String species,
                                                                 String city,
                                                                 String animalSize,
                                                                 Character sex) {
        return Specification.where(hasStatus(status))
                .and(hasSpecies(species))
                .and(hasCity(city))
                .and(hasAnimalSize(animalSize))
                .and(hasSex(sex));
    }

    private Specification<Animal> hasStatus(AnimalStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    private Specification<Animal> hasSpecies(String species) {
        return (root, query, criteriaBuilder) ->
                isBlank(species)
                        ? null
                        : criteriaBuilder.equal(criteriaBuilder.upper(root.get("species")), species.trim().toUpperCase());
    }

    private Specification<Animal> hasCity(String city) {
        return (root, query, criteriaBuilder) ->
                isBlank(city)
                        ? null
                        : criteriaBuilder.equal(
                        criteriaBuilder.upper(root.join("user", JoinType.LEFT).get("city")),
                        city.trim().toUpperCase()
                );
    }

    private Specification<Animal> hasAnimalSize(String animalSize) {
        return (root, query, criteriaBuilder) ->
                isBlank(animalSize)
                        ? null
                        : criteriaBuilder.equal(
                        criteriaBuilder.upper(root.get("animalSize")),
                        animalSize.trim().toUpperCase()
                );
    }

    private Specification<Animal> hasSex(Character sex) {
        return (root, query, criteriaBuilder) ->
                sex == null ? null : criteriaBuilder.equal(root.get("sex"), sex);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
