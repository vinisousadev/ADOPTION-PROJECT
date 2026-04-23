package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateAnimalPhotoRequest;
import br.com.adoption.dto.request.PatchAnimalPhotoRequest;
import br.com.adoption.dto.request.UpdateAnimalPhotoRequest;
import br.com.adoption.dto.response.AnimalPhotoResponse;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.AnimalPhoto;
import br.com.adoption.entity.User;
import br.com.adoption.entity.UserType;
import br.com.adoption.exception.OnlyOwnerCanManageAnimalException;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.mapper.AnimalPhotoMapper;
import br.com.adoption.repository.AnimalPhotoRepository;
import br.com.adoption.repository.AnimalRepository;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.AnimalPhotoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalPhotoServiceImpl implements AnimalPhotoService {

    private final AnimalPhotoRepository animalPhotoRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;

    public AnimalPhotoServiceImpl(AnimalPhotoRepository animalPhotoRepository,
                                  AnimalRepository animalRepository,
                                  UserRepository userRepository) {
        this.animalPhotoRepository = animalPhotoRepository;
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AnimalPhotoResponse> getAllPhotos() {
        return AnimalPhotoMapper.toResponseList(animalPhotoRepository.findAll(Sort.by("id")));
    }

    @Override
    public Page<AnimalPhotoResponse> getAllPhotos(Pageable pageable) {
        return animalPhotoRepository.findAll(pageable).map(AnimalPhotoMapper::toResponse);
    }

    @Override
    public Page<AnimalPhotoResponse> getAllPhotos(Long animalId, Pageable pageable) {
        if (animalId == null) {
            return getAllPhotos(pageable);
        }
        return animalPhotoRepository.findByAnimal_Id(animalId, pageable).map(AnimalPhotoMapper::toResponse);
    }

    @Override
    public AnimalPhotoResponse getById(Long photoId) {
        AnimalPhoto animalPhoto = animalPhotoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal photo not found"));
        return AnimalPhotoMapper.toResponse(animalPhoto);
    }

    @Override
    public AnimalPhotoResponse save(CreateAnimalPhotoRequest request, String userEmail) {
        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateOwnerOrAdmin(animal, authenticatedUser);

        AnimalPhoto animalPhoto = AnimalPhotoMapper.toEntity(request);
        animalPhoto.setAnimal(animal);

        AnimalPhoto savedPhoto = animalPhotoRepository.save(animalPhoto);
        return AnimalPhotoMapper.toResponse(savedPhoto);
    }

    @Override
    public AnimalPhotoResponse update(Long photoId, UpdateAnimalPhotoRequest request, String userEmail) {
        AnimalPhoto animalPhoto = animalPhotoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal photo not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateOwnerOrAdmin(animalPhoto.getAnimal(), authenticatedUser);

        animalPhoto.setPhotoUrl(request.getPhotoUrl());
        animalPhoto.setIsMain(request.getIsMain());

        AnimalPhoto updatedPhoto = animalPhotoRepository.save(animalPhoto);
        return AnimalPhotoMapper.toResponse(updatedPhoto);
    }

    @Override
    public AnimalPhotoResponse patch(Long photoId, PatchAnimalPhotoRequest request, String userEmail) {
        AnimalPhoto animalPhoto = animalPhotoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal photo not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateOwnerOrAdmin(animalPhoto.getAnimal(), authenticatedUser);

        if (request.getPhotoUrl() != null) {
            animalPhoto.setPhotoUrl(request.getPhotoUrl());
        }

        if (request.getIsMain() != null) {
            animalPhoto.setIsMain(request.getIsMain());
        }

        AnimalPhoto updatedPhoto = animalPhotoRepository.save(animalPhoto);
        return AnimalPhotoMapper.toResponse(updatedPhoto);
    }

    @Override
    public AnimalPhotoResponse delete(Long photoId, String userEmail) {
        AnimalPhoto animalPhoto = animalPhotoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal photo not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateOwnerOrAdmin(animalPhoto.getAnimal(), authenticatedUser);
        animalPhotoRepository.delete(animalPhoto);

        return AnimalPhotoMapper.toResponse(animalPhoto);
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
