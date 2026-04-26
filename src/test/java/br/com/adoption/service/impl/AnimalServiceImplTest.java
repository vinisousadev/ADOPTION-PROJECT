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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

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
        animal1.setStatus(AnimalStatus.AVAILABLE);

        Animal animal2 = new Animal();
        animal2.setAnimalName("Mia");
        animal2.setSpecies("Cat");
        animal2.setStatus(AnimalStatus.AVAILABLE);

        when(animalRepository.findByStatus(AnimalStatus.AVAILABLE)).thenReturn(List.of(animal1, animal2));

        List<AnimalResponse> result = animalService.getAvailableAnimals();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Rex", result.get(0).getAnimalName());
        assertEquals("Mia", result.get(1).getAnimalName());
        assertEquals(AnimalStatus.AVAILABLE, result.get(0).getStatus());

        verify(animalRepository, times(1)).findByStatus(AnimalStatus.AVAILABLE);
    }

    @Test
    void shouldReturnFilteredAvailableAnimals() {
        Animal animal = new Animal();
        animal.setAnimalName("Mia");
        animal.setSpecies("Cat");
        animal.setStatus(AnimalStatus.AVAILABLE);

        when(animalRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(animal)));

        Page<AnimalResponse> result = animalService.getAvailableAnimals(
                PageRequest.of(0, 10),
                "Cat",
                "Sao Paulo",
                "SMALL",
                'F'
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Mia", result.getContent().getFirst().getAnimalName());

        verify(animalRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void shouldReturnAuthenticatedUserAnimalsOrderedById() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        Animal animal1 = new Animal();
        animal1.setAnimalName("Rex");
        animal1.setSpecies("Dog");

        Animal animal2 = new Animal();
        animal2.setAnimalName("Nina");
        animal2.setSpecies("Cat");

        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(user));
        when(animalRepository.findByUser_IdOrderById(1L)).thenReturn(List.of(animal1, animal2));

        List<AnimalResponse> result = animalService.getMyAnimals("owner@email.com");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Rex", result.get(0).getAnimalName());
        assertEquals("Nina", result.get(1).getAnimalName());

        verify(animalRepository, times(1)).findByUser_IdOrderById(1L);
    }

    @Test
    void shouldThrowExceptionWhenGettingMyAnimalsAndUserDoesNotExist() {
        when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> animalService.getMyAnimals("missing@email.com")
        );

        assertEquals("User not found", exception.getMessage());
        verify(animalRepository, never()).findByUser_IdOrderById(anyLong());
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
    void shouldReturnFilteredAnimalsForAdminList() {
        Animal animal = new Animal();
        animal.setAnimalName("Rex");
        animal.setSpecies("Dog");
        animal.setStatus(AnimalStatus.ADOPTED);

        when(animalRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(animal)));

        Page<AnimalResponse> result = animalService.getAllAnimals(
                PageRequest.of(0, 10),
                AnimalStatus.ADOPTED,
                "Dog",
                "Campinas",
                "MEDIUM",
                'M'
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(AnimalStatus.ADOPTED, result.getContent().getFirst().getStatus());

        verify(animalRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void shouldReturnAvailableAnimalByIdWithoutAuthenticatedUser() {
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = new Animal();
        animal.setAnimalName("Rex");
        animal.setSpecies("Dog");
        animal.setStatus(AnimalStatus.AVAILABLE);
        animal.setRegistrationDate(LocalDateTime.now());
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));

        AnimalResponse result = animalService.getById(10L, null);

        assertNotNull(result);
        assertEquals("Rex", result.getAnimalName());
        assertEquals("Dog", result.getSpecies());
        assertEquals(AnimalStatus.AVAILABLE, result.getStatus());
        assertEquals(1L, result.getUserId());

        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void shouldReturnUnavailableAnimalByIdWhenAuthenticatedUserIsOwner() {
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);
        when(owner.getUserType()).thenReturn(UserType.COMMON);

        Animal animal = new Animal();
        animal.setAnimalName("Rex");
        animal.setSpecies("Dog");
        animal.setStatus(AnimalStatus.ADOPTED);
        animal.setRegistrationDate(LocalDateTime.now());
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(owner));

        AnimalResponse result = animalService.getById(10L, "owner@email.com");

        assertNotNull(result);
        assertEquals("Rex", result.getAnimalName());
        assertEquals(AnimalStatus.ADOPTED, result.getStatus());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void shouldThrowNotFoundWhenUnavailableAnimalIsRequestedByNonOwner() {
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User intruder = mock(User.class);
        when(intruder.getId()).thenReturn(99L);
        when(intruder.getUserType()).thenReturn(UserType.COMMON);

        Animal animal = new Animal();
        animal.setStatus(AnimalStatus.ADOPTED);
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findByEmail("intruder@email.com")).thenReturn(Optional.of(intruder));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> animalService.getById(10L, "intruder@email.com")
        );

        assertEquals("Animal not found", exception.getMessage());
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

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(owner));
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnimalResponse result = animalService.save(request, "owner@email.com");

        ArgumentCaptor<Animal> animalCaptor = ArgumentCaptor.forClass(Animal.class);
        verify(animalRepository, times(1)).save(animalCaptor.capture());

        Animal capturedAnimal = animalCaptor.getValue();

        assertNotNull(result);
        assertEquals("Rex", result.getAnimalName());
        assertEquals("Dog", result.getSpecies());
        assertEquals(AnimalStatus.AVAILABLE, result.getStatus());
        assertEquals(1L, result.getUserId());

        assertEquals("Rex", capturedAnimal.getAnimalName());
        assertEquals("Dog", capturedAnimal.getSpecies());
        assertEquals(AnimalStatus.AVAILABLE, capturedAnimal.getStatus());
        assertEquals(owner, capturedAnimal.getUser());
        assertNotNull(capturedAnimal.getRegistrationDate());
    }

    @Test
    void shouldThrowExceptionWhenOwnerDoesNotExist() {
        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setAnimalName("Rex");
        request.setSpecies("Dog");

        when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> animalService.save(request, "missing@email.com")
        );

        assertEquals("User not found", exception.getMessage());
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    void shouldUpdateAnimalWhenAuthenticatedUserIsOwner() {
        UpdateAnimalRequest request = new UpdateAnimalRequest();
        request.setAnimalName("Rex atualizado");
        request.setSpecies("Dog");
        request.setBreed("Labrador");
        request.setBirthDate(LocalDate.of(2024, 1, 10));
        request.setAge(2);
        request.setAnimalSize("MEDIUM");
        request.setSex('M');
        request.setWeightKg(new BigDecimal("14.50"));
        request.setVaccinated('Y');
        request.setNeutered('Y');
        request.setDescription("Very friendly");

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);
        when(owner.getUserType()).thenReturn(UserType.COMMON);

        Animal animal = new Animal();
        animal.setAnimalName("Rex");
        animal.setSpecies("Dog");
        animal.setStatus(AnimalStatus.AVAILABLE);
        animal.setRegistrationDate(LocalDateTime.now());
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(owner));
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnimalResponse result = animalService.update(10L, request, "owner@email.com");

        assertNotNull(result);
        assertEquals("Rex atualizado", result.getAnimalName());
        assertEquals("Dog", result.getSpecies());
        assertEquals("Labrador", result.getBreed());
        assertEquals(2, result.getAge());
        assertEquals(AnimalStatus.AVAILABLE, result.getStatus());
        assertEquals(1L, result.getUserId());

        verify(animalRepository, times(1)).save(animal);
    }

    @Test
    void shouldUpdateAnimalWhenAuthenticatedUserIsAdmin() {
        UpdateAnimalRequest request = new UpdateAnimalRequest();
        request.setAnimalName("Mia atualizada");
        request.setSpecies("Cat");
        request.setVaccinated('Y');
        request.setNeutered('N');

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User admin = mock(User.class);
        when(admin.getUserType()).thenReturn(UserType.ADMIN);

        Animal animal = new Animal();
        animal.setAnimalName("Mia");
        animal.setSpecies("Cat");
        animal.setStatus(AnimalStatus.AVAILABLE);
        animal.setRegistrationDate(LocalDateTime.now());
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnimalResponse result = animalService.update(10L, request, "admin@email.com");

        assertNotNull(result);
        assertEquals("Mia atualizada", result.getAnimalName());
        assertEquals("Cat", result.getSpecies());

        verify(animalRepository, times(1)).save(animal);
    }

    @Test
    void shouldPatchAnimalWhenAuthenticatedUserIsOwner() {
        PatchAnimalRequest request = new PatchAnimalRequest();
        request.setDescription("Updated by patch");

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);
        when(owner.getUserType()).thenReturn(UserType.COMMON);

        Animal animal = new Animal();
        animal.setAnimalName("Rex");
        animal.setSpecies("Dog");
        animal.setDescription("Old");
        animal.setStatus(AnimalStatus.AVAILABLE);
        animal.setRegistrationDate(LocalDateTime.now());
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(owner));
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnimalResponse result = animalService.patch(10L, request, "owner@email.com");

        assertNotNull(result);
        assertEquals("Updated by patch", result.getDescription());
        assertEquals("Rex", result.getAnimalName());

        verify(animalRepository, times(1)).save(animal);
    }

    @Test
    void shouldThrowExceptionWhenNonOwnerTriesToUpdateAnimal() {
        UpdateAnimalRequest request = new UpdateAnimalRequest();
        request.setAnimalName("Rex atualizado");
        request.setSpecies("Dog");
        request.setVaccinated('Y');
        request.setNeutered('Y');

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User intruder = mock(User.class);
        when(intruder.getId()).thenReturn(99L);
        when(intruder.getUserType()).thenReturn(UserType.COMMON);

        Animal animal = new Animal();
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findByEmail("intruder@email.com")).thenReturn(Optional.of(intruder));

        OnlyOwnerCanManageAnimalException exception = assertThrows(
                OnlyOwnerCanManageAnimalException.class,
                () -> animalService.update(10L, request, "intruder@email.com")
        );

        assertEquals("Only the animal owner or admin can manage this animal", exception.getMessage());
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    void shouldSoftDeleteAnimalWhenAuthenticatedUserIsOwner() {
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);
        when(owner.getUserType()).thenReturn(UserType.COMMON);

        Animal animal = new Animal();
        animal.setAnimalName("Rex");
        animal.setSpecies("Dog");
        animal.setStatus(AnimalStatus.AVAILABLE);
        animal.setRegistrationDate(LocalDateTime.now());
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(owner));
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnimalResponse result = animalService.delete(10L, "owner@email.com");

        assertNotNull(result);
        assertEquals(AnimalStatus.REMOVED, result.getStatus());
        assertEquals(AnimalStatus.REMOVED, animal.getStatus());
        assertEquals(1L, result.getUserId());

        verify(animalRepository, times(1)).save(animal);
    }

    @Test
    void shouldSoftDeleteAnimalWhenAuthenticatedUserIsAdmin() {
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User admin = mock(User.class);
        when(admin.getUserType()).thenReturn(UserType.ADMIN);

        Animal animal = new Animal();
        animal.setAnimalName("Rex");
        animal.setSpecies("Dog");
        animal.setStatus(AnimalStatus.AVAILABLE);
        animal.setRegistrationDate(LocalDateTime.now());
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(admin));
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnimalResponse result = animalService.delete(10L, "admin@email.com");

        assertNotNull(result);
        assertEquals(AnimalStatus.REMOVED, result.getStatus());

        verify(animalRepository, times(1)).save(animal);
    }

    @Test
    void shouldThrowExceptionWhenNonOwnerTriesToDeleteAnimal() {
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User intruder = mock(User.class);
        when(intruder.getId()).thenReturn(99L);
        when(intruder.getUserType()).thenReturn(UserType.COMMON);

        Animal animal = new Animal();
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findByEmail("intruder@email.com")).thenReturn(Optional.of(intruder));

        OnlyOwnerCanManageAnimalException exception = assertThrows(
                OnlyOwnerCanManageAnimalException.class,
                () -> animalService.delete(10L, "intruder@email.com")
        );

        assertEquals("Only the animal owner or admin can manage this animal", exception.getMessage());
        verify(animalRepository, never()).save(any(Animal.class));
    }
}
