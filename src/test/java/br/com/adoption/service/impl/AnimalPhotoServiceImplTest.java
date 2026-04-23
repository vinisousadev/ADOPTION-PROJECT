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
import br.com.adoption.repository.AnimalPhotoRepository;
import br.com.adoption.repository.AnimalRepository;
import br.com.adoption.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalPhotoServiceImplTest {

    @Mock
    private AnimalPhotoRepository animalPhotoRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AnimalPhotoServiceImpl animalPhotoService;

    @Test
    void shouldReturnAllPhotosOrderedById() {
        AnimalPhoto photo1 = new AnimalPhoto();
        photo1.setPhotoUrl("https://img.com/photo1.jpg");
        photo1.setIsMain('Y');

        AnimalPhoto photo2 = new AnimalPhoto();
        photo2.setPhotoUrl("https://img.com/photo2.jpg");
        photo2.setIsMain('N');

        when(animalPhotoRepository.findAll(Sort.by("id"))).thenReturn(List.of(photo1, photo2));

        List<AnimalPhotoResponse> result = animalPhotoService.getAllPhotos();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("https://img.com/photo1.jpg", result.get(0).getPhotoUrl());
        assertEquals("https://img.com/photo2.jpg", result.get(1).getPhotoUrl());

        verify(animalPhotoRepository, times(1)).findAll(Sort.by("id"));
    }

    @Test
    void shouldReturnAllPhotosFilteredByAnimalId() {
        Animal animal = spy(new Animal());
        doReturn(10L).when(animal).getId();

        AnimalPhoto photo1 = new AnimalPhoto();
        photo1.setPhotoUrl("https://img.com/photo1.jpg");
        photo1.setIsMain('Y');
        photo1.setAnimal(animal);

        AnimalPhoto photo2 = new AnimalPhoto();
        photo2.setPhotoUrl("https://img.com/photo2.jpg");
        photo2.setIsMain('N');
        photo2.setAnimal(animal);

        PageRequest pageable = PageRequest.of(0, 10);
        Page<AnimalPhoto> page = new PageImpl<>(List.of(photo1, photo2), pageable, 2);

        when(animalPhotoRepository.findByAnimal_Id(10L, pageable)).thenReturn(page);

        Page<AnimalPhotoResponse> result = animalPhotoService.getAllPhotos(10L, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(10L, result.getContent().get(0).getAnimalId());
        assertEquals(10L, result.getContent().get(1).getAnimalId());
        verify(animalPhotoRepository, times(1)).findByAnimal_Id(10L, pageable);
    }

    @Test
    void shouldReturnPhotoById() {
        Animal animal = spy(new Animal());
        doReturn(1L).when(animal).getId();

        AnimalPhoto photo = new AnimalPhoto();
        photo.setPhotoUrl("https://img.com/photo1.jpg");
        photo.setIsMain('Y');
        photo.setAnimal(animal);

        when(animalPhotoRepository.findById(1L)).thenReturn(Optional.of(photo));

        AnimalPhotoResponse result = animalPhotoService.getById(1L);

        assertNotNull(result);
        assertEquals("https://img.com/photo1.jpg", result.getPhotoUrl());
        assertEquals('Y', result.getIsMain());
        assertEquals(1L, result.getAnimalId());
    }

    @Test
    void shouldThrowExceptionWhenPhotoByIdDoesNotExist() {
        when(animalPhotoRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> animalPhotoService.getById(99L)
        );

        assertEquals("Animal photo not found", exception.getMessage());
    }

    @Test
    void shouldSavePhotoWithExistingAnimal() {
        CreateAnimalPhotoRequest request = new CreateAnimalPhotoRequest();
        request.setPhotoUrl("https://img.com/rex-main.jpg");
        request.setIsMain('Y');
        request.setAnimalId(1L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = spy(new Animal());
        doReturn(1L).when(animal).getId();
        animal.setUser(owner);

        User authenticatedUser = mock(User.class);
        when(authenticatedUser.getId()).thenReturn(1L);
        when(authenticatedUser.getUserType()).thenReturn(UserType.COMMON);

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(authenticatedUser));
        when(animalPhotoRepository.save(any(AnimalPhoto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnimalPhotoResponse result = animalPhotoService.save(request, "owner@email.com");

        ArgumentCaptor<AnimalPhoto> photoCaptor = ArgumentCaptor.forClass(AnimalPhoto.class);
        verify(animalPhotoRepository, times(1)).save(photoCaptor.capture());

        AnimalPhoto capturedPhoto = photoCaptor.getValue();

        assertNotNull(result);
        assertEquals("https://img.com/rex-main.jpg", result.getPhotoUrl());
        assertEquals('Y', result.getIsMain());
        assertEquals(1L, result.getAnimalId());

        assertEquals("https://img.com/rex-main.jpg", capturedPhoto.getPhotoUrl());
        assertEquals('Y', capturedPhoto.getIsMain());
        assertEquals(animal, capturedPhoto.getAnimal());
    }

    @Test
    void shouldThrowExceptionWhenAnimalDoesNotExist() {
        CreateAnimalPhotoRequest request = new CreateAnimalPhotoRequest();
        request.setPhotoUrl("https://img.com/rex-main.jpg");
        request.setIsMain('Y');
        request.setAnimalId(99L);

        when(animalRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> animalPhotoService.save(request, "owner@email.com")
        );

        assertEquals("Animal not found", exception.getMessage());
        verify(animalPhotoRepository, never()).save(any(AnimalPhoto.class));
    }

    @Test
    void shouldUpdatePhoto() {
        UpdateAnimalPhotoRequest request = new UpdateAnimalPhotoRequest();
        request.setPhotoUrl("https://img.com/rex-updated.jpg");
        request.setIsMain('N');

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User authenticatedUser = mock(User.class);
        when(authenticatedUser.getId()).thenReturn(1L);
        when(authenticatedUser.getUserType()).thenReturn(UserType.COMMON);

        Animal animal = new Animal();
        animal.setUser(owner);

        AnimalPhoto photo = new AnimalPhoto();
        photo.setPhotoUrl("https://img.com/rex-main.jpg");
        photo.setIsMain('Y');
        photo.setAnimal(animal);

        when(animalPhotoRepository.findById(1L)).thenReturn(Optional.of(photo));
        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(authenticatedUser));
        when(animalPhotoRepository.save(any(AnimalPhoto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnimalPhotoResponse result = animalPhotoService.update(1L, request, "owner@email.com");

        assertNotNull(result);
        assertEquals("https://img.com/rex-updated.jpg", result.getPhotoUrl());
        assertEquals('N', result.getIsMain());

        verify(animalPhotoRepository, times(1)).save(photo);
    }

    @Test
    void shouldPatchPhoto() {
        PatchAnimalPhotoRequest request = new PatchAnimalPhotoRequest();
        request.setPhotoUrl("https://img.com/rex-patched.jpg");

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User authenticatedUser = mock(User.class);
        when(authenticatedUser.getId()).thenReturn(1L);
        when(authenticatedUser.getUserType()).thenReturn(UserType.COMMON);

        Animal animal = new Animal();
        animal.setUser(owner);

        AnimalPhoto photo = new AnimalPhoto();
        photo.setPhotoUrl("https://img.com/rex-main.jpg");
        photo.setIsMain('Y');
        photo.setAnimal(animal);

        when(animalPhotoRepository.findById(1L)).thenReturn(Optional.of(photo));
        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(authenticatedUser));
        when(animalPhotoRepository.save(any(AnimalPhoto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnimalPhotoResponse result = animalPhotoService.patch(1L, request, "owner@email.com");

        assertNotNull(result);
        assertEquals("https://img.com/rex-patched.jpg", result.getPhotoUrl());
        assertEquals('Y', result.getIsMain());

        verify(animalPhotoRepository, times(1)).save(photo);
    }

    @Test
    void shouldDeletePhotoWhenAuthenticatedUserIsOwner() {
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);
        when(owner.getUserType()).thenReturn(UserType.COMMON);

        Animal animal = new Animal();
        animal.setUser(owner);

        AnimalPhoto photo = new AnimalPhoto();
        photo.setAnimal(animal);

        when(animalPhotoRepository.findById(1L)).thenReturn(Optional.of(photo));
        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(owner));

        AnimalPhotoResponse result = animalPhotoService.delete(1L, "owner@email.com");

        assertNotNull(result);
        verify(animalPhotoRepository, times(1)).delete(photo);
    }

    @Test
    void shouldThrowExceptionWhenNonOwnerTriesToDeletePhoto() {
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User intruder = mock(User.class);
        when(intruder.getId()).thenReturn(99L);
        when(intruder.getUserType()).thenReturn(UserType.COMMON);

        Animal animal = new Animal();
        animal.setUser(owner);

        AnimalPhoto photo = new AnimalPhoto();
        photo.setAnimal(animal);

        when(animalPhotoRepository.findById(1L)).thenReturn(Optional.of(photo));
        when(userRepository.findByEmail("intruder@email.com")).thenReturn(Optional.of(intruder));

        OnlyOwnerCanManageAnimalException exception = assertThrows(
                OnlyOwnerCanManageAnimalException.class,
                () -> animalPhotoService.delete(1L, "intruder@email.com")
        );

        assertEquals("Only the animal owner or admin can manage this animal", exception.getMessage());
        verify(animalPhotoRepository, never()).delete(any(AnimalPhoto.class));
    }
}
