package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateAnimalPhotoRequest;
import br.com.adoption.dto.response.AnimalPhotoResponse;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.AnimalPhoto;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.repository.AnimalPhotoRepository;
import br.com.adoption.repository.AnimalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void shouldSavePhotoWithExistingAnimal() {
        CreateAnimalPhotoRequest request = new CreateAnimalPhotoRequest();
        request.setPhotoUrl("https://img.com/rex-main.jpg");
        request.setIsMain('Y');
        request.setAnimalId(1L);

        Animal animal = mock(Animal.class);
        when(animal.getId()).thenReturn(1L);

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(animalPhotoRepository.save(any(AnimalPhoto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnimalPhotoResponse result = animalPhotoService.save(request);

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
                () -> animalPhotoService.save(request)
        );

        assertEquals("Animal not found", exception.getMessage());
        verify(animalPhotoRepository, never()).save(any(AnimalPhoto.class));
    }
}