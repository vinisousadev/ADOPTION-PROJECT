package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateAnimalPhotoRequest;
import br.com.adoption.dto.request.PatchAnimalPhotoRequest;
import br.com.adoption.dto.request.UpdateAnimalPhotoRequest;
import br.com.adoption.dto.response.AnimalPhotoResponse;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.AnimalPhoto;
import br.com.adoption.entity.User;
import br.com.adoption.entity.UserType;
import br.com.adoption.exception.InvalidFileUploadException;
import br.com.adoption.exception.OnlyOwnerCanManageAnimalException;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.mapper.AnimalPhotoMapper;
import br.com.adoption.repository.AnimalPhotoRepository;
import br.com.adoption.repository.AnimalRepository;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.AnimalPhotoService;
import br.com.adoption.service.SupabaseStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AnimalPhotoServiceImpl implements AnimalPhotoService {

    private static final long MAX_PHOTO_SIZE = 4 * 1024 * 1024;
    private static final Map<String, String> ALLOWED_PHOTO_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final AnimalPhotoRepository animalPhotoRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;
    private final SupabaseStorageService supabaseStorageService;

    public AnimalPhotoServiceImpl(AnimalPhotoRepository animalPhotoRepository,
                                  AnimalRepository animalRepository,
                                  UserRepository userRepository,
                                  SupabaseStorageService supabaseStorageService) {
        this.animalPhotoRepository = animalPhotoRepository;
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
        this.supabaseStorageService = supabaseStorageService;
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
    public AnimalPhotoResponse upload(Long animalId, Character isMain, MultipartFile file, String userEmail) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateOwnerOrAdmin(animal, authenticatedUser);
        validatePhoto(file, "Animal photo");

        String contentType = file.getContentType();
        String extension = ALLOWED_PHOTO_TYPES.get(contentType);

        try {
            String photoUrl = supabaseStorageService.uploadAnimalPhoto(
                    animal.getId(),
                    file.getBytes(),
                    contentType,
                    extension
            );

            AnimalPhoto animalPhoto = new AnimalPhoto();
            animalPhoto.setAnimal(animal);
            animalPhoto.setPhotoUrl(photoUrl);
            animalPhoto.setIsMain(normalizeIsMain(isMain));

            AnimalPhoto savedPhoto = animalPhotoRepository.save(animalPhoto);
            return AnimalPhotoMapper.toResponse(savedPhoto);
        } catch (IOException exception) {
            throw new InvalidFileUploadException("Could not read animal photo file");
        }
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

    private void validatePhoto(MultipartFile file, String label) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileUploadException(label + " is required");
        }

        if (file.getSize() > MAX_PHOTO_SIZE) {
            throw new InvalidFileUploadException(label + " must be at most 4MB");
        }

        if (!ALLOWED_PHOTO_TYPES.containsKey(file.getContentType())) {
            throw new InvalidFileUploadException(label + " must be JPG, PNG or WEBP");
        }
    }

    private Character normalizeIsMain(Character isMain) {
        if (isMain == null) {
            return 'N';
        }

        return Character.toUpperCase(isMain);
    }
}
