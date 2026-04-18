package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateAnimalRequest;
import br.com.adoption.dto.response.AnimalResponse;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.User;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.repository.AnimalRepository;
import br.com.adoption.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceImplTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AnimalServiceImpl animalService;

    @Test
    void shouldReturnAvailableAnimals() {
        Animal animal1 = new Animal();
        animal1.setAnimalName("Rex");
        animal1.setSpecies("Dog");
        animal1.setStatus("AVAILABLE");

        Animal animal2 = new Animal();
        animal2.setAnimalName("Mia");
        animal2.setSpecies("Cat");
        animal2.setStatus("AVAILABLE");

        when(animalRepository.findByStatus("AVAILABLE")).thenReturn(List.of(animal1, animal2));

        List<AnimalResponse> result = animalService.getAvailableAnimals();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Rex", result.get(0).getAnimalName());
        assertEquals("Mia", result.get(1).getAnimalName());
        assertEquals("AVAILABLE", result.get(0).getStatus());

        verify(animalRepository, times(1)).findByStatus("AVAILABLE");
    }

    @Test
    void shouldReturnAllAnimalsOrderedById() {
        Animal animal1 = new Animal();
        animal1.setAnimalName("Rex");
        animal1.setSpecies("Dog");

        Animal animal2 = new Animal();
        animal2.setAnimalName("Mia");
        animal2.setSpecies("Cat");

        when(animalRepository.findAll(Sort.by("id"))).thenReturn(List.of(animal1, animal2));

        List<AnimalResponse> result = animalService.getAllAnimals();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Rex", result.get(0).getAnimalName());
        assertEquals("Mia", result.get(1).getAnimalName());

        verify(animalRepository, times(1)).findAll(Sort.by("id"));
    }

    @Test
    void shouldSaveAnimalWithExistingOwnerAndForceAvailableStatus() {
        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setAnimalName("Rex");
        request.setSpecies("Dog");
        request.setBreed("Labrador");
        request.setBirthDate(LocalDate.of(2024, 1, 10));
        request.setAge(1);
        request.setAnimalSize("MEDIUM");
        request.setSex('M');
        request.setWeightKg(new BigDecimal("12.50"));
        request.setVaccinated('Y');
        request.setNeutered('N');
        request.setDescription("Very friendly");
        request.setRegistrationDate(LocalDateTime.now());
        request.setUserId(1L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnimalResponse result = animalService.save(request);

        ArgumentCaptor<Animal> animalCaptor = ArgumentCaptor.forClass(Animal.class);
        verify(animalRepository, times(1)).save(animalCaptor.capture());

        Animal capturedAnimal = animalCaptor.getValue();

        assertNotNull(result);
        assertEquals("Rex", result.getAnimalName());
        assertEquals("Dog", result.getSpecies());
        assertEquals("AVAILABLE", result.getStatus());
        assertEquals(1L, result.getUserId());

        assertEquals("Rex", capturedAnimal.getAnimalName());
        assertEquals("Dog", capturedAnimal.getSpecies());
        assertEquals("AVAILABLE", capturedAnimal.getStatus());
        assertEquals(owner, capturedAnimal.getUser());
    }

    @Test
    void shouldThrowExceptionWhenOwnerDoesNotExist() {
        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setAnimalName("Rex");
        request.setSpecies("Dog");
        request.setUserId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> animalService.save(request)
        );

        assertEquals("User not found", exception.getMessage());
        verify(animalRepository, never()).save(any(Animal.class));
    }
}