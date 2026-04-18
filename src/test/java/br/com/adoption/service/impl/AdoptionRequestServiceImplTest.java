package br.com.adoption.service.impl;

import br.com.adoption.entity.AdoptionRequest;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.User;
import br.com.adoption.exception.OwnerCannotAdoptOwnAnimalException;
import br.com.adoption.repository.AdoptionRequestRepository;
import br.com.adoption.repository.AnimalRepository;
import br.com.adoption.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import br.com.adoption.entity.AdoptionRequestStatus;
import br.com.adoption.exception.DuplicateAdoptionRequestException;
import br.com.adoption.exception.AnimalNotAvailableException;
import br.com.adoption.exception.OnlyOwnerCanManageAdoptionRequestException;
import br.com.adoption.exception.AdoptionRequestNotPendingException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdoptionRequestServiceImplTest {

    @Mock
    private AdoptionRequestRepository adoptionRequestRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdoptionRequestServiceImpl adoptionRequestService;

    @Test
    void shouldThrowExceptionWhenOwnerTriesToAdoptOwnAnimal() throws Exception {
        User owner = new User();
        setId(owner, 1L);
        owner.setName("Carlos Owner");

        Animal animal = new Animal();
        setId(animal, 10L);
        animal.setAnimalName("Thor");
        animal.setStatus("AVAILABLE");
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setAnimal(animal);
        request.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        assertThrows(
                OwnerCannotAdoptOwnAnimalException.class,
                () -> adoptionRequestService.save(request)
        );
    }

    private void setId(Object obj, Long idValue) throws Exception {
        Field field = obj.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(obj, idValue);
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyHasPendingRequestForSameAnimal() throws Exception {
        User owner = new User();
        setId(owner, 1L);
        owner.setName("Carlos Owner");

        User adopter = new User();
        setId(adopter, 2L);
        adopter.setName("Ana Adopter");

        Animal animal = new Animal();
        setId(animal, 10L);
        animal.setAnimalName("Thor");
        animal.setStatus("AVAILABLE");
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setAnimal(animal);
        request.setUser(adopter);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adopter));
        when(adoptionRequestRepository.existsByAnimal_IdAndUser_IdAndStatus(
                10L, 2L, AdoptionRequestStatus.PENDING
        )).thenReturn(true);

        assertThrows(
                DuplicateAdoptionRequestException.class,
                () -> adoptionRequestService.save(request)
        );
    }

    @Test
    void shouldThrowExceptionWhenAnimalIsNotAvailable() throws Exception {
        User owner = new User();
        setId(owner, 1L);
        owner.setName("Carlos Owner");

        User adopter = new User();
        setId(adopter, 2L);
        adopter.setName("Ana Adopter");

        Animal animal = new Animal();
        setId(animal, 10L);
        animal.setAnimalName("Thor");
        animal.setStatus("ADOPTED");
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setAnimal(animal);
        request.setUser(adopter);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));

        assertThrows(
                AnimalNotAvailableException.class,
                () -> adoptionRequestService.save(request)
        );
    }
    @Test
    void shouldThrowExceptionWhenNonOwnerTriesToApproveRequest() throws Exception {
        User owner = new User();
        setId(owner, 1L);
        owner.setName("Carlos Owner");

        User adopter = new User();
        setId(adopter, 2L);
        adopter.setName("Ana Adopter");

        User anotherUser = new User();
        setId(anotherUser, 3L);
        anotherUser.setName("Bruno Adopter");

        Animal animal = new Animal();
        setId(animal, 10L);
        animal.setAnimalName("Bob");
        animal.setStatus("AVAILABLE");
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        setId(request, 100L);
        request.setAnimal(animal);
        request.setUser(adopter);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(adoptionRequestRepository.findById(100L)).thenReturn(Optional.of(request));

        assertThrows(
                OnlyOwnerCanManageAdoptionRequestException.class,
                () -> adoptionRequestService.approveRequest(100L, 3L)
        );
    }
    @Test
    void shouldThrowExceptionWhenTryingToApproveRequestThatIsNotPending() throws Exception {
        User owner = new User();
        setId(owner, 1L);
        owner.setName("Carlos Owner");

        User adopter = new User();
        setId(adopter, 2L);
        adopter.setName("Ana Adopter");

        Animal animal = new Animal();
        setId(animal, 10L);
        animal.setAnimalName("Bob");
        animal.setStatus("AVAILABLE");
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        setId(request, 100L);
        request.setAnimal(animal);
        request.setUser(adopter);
        request.setStatus(AdoptionRequestStatus.APPROVED);

        when(adoptionRequestRepository.findById(100L)).thenReturn(Optional.of(request));

        assertThrows(
                AdoptionRequestNotPendingException.class,
                () -> adoptionRequestService.approveRequest(100L, 1L)
        );
    }
    @Test
    void shouldApproveRequestAndRejectOtherPendingRequestsForSameAnimal() throws Exception {
        User owner = new User();
        setId(owner, 1L);
        owner.setName("Carlos Owner");

        User adopter1 = new User();
        setId(adopter1, 2L);
        adopter1.setName("Ana Adopter");

        User adopter2 = new User();
        setId(adopter2, 3L);
        adopter2.setName("Bruno Adopter");

        Animal animal = new Animal();
        setId(animal, 10L);
        animal.setAnimalName("Bob");
        animal.setStatus("AVAILABLE");
        animal.setUser(owner);

        AdoptionRequest requestToApprove = new AdoptionRequest();
        setId(requestToApprove, 100L);
        requestToApprove.setAnimal(animal);
        requestToApprove.setUser(adopter1);
        requestToApprove.setStatus(AdoptionRequestStatus.PENDING);

        AdoptionRequest otherPendingRequest = new AdoptionRequest();
        setId(otherPendingRequest, 101L);
        otherPendingRequest.setAnimal(animal);
        otherPendingRequest.setUser(adopter2);
        otherPendingRequest.setStatus(AdoptionRequestStatus.PENDING);

        when(adoptionRequestRepository.findById(100L)).thenReturn(Optional.of(requestToApprove));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(animalRepository.save(any(Animal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(adoptionRequestRepository.findByAnimal_IdAndStatus(10L, AdoptionRequestStatus.PENDING))
                .thenReturn(List.of(otherPendingRequest));

        AdoptionRequest result = adoptionRequestService.approveRequest(100L, 1L);

        assertEquals(AdoptionRequestStatus.APPROVED, result.getStatus());
        assertNotNull(result.getResponseDate());

        assertEquals("ADOPTED", animal.getStatus());

        assertEquals(AdoptionRequestStatus.REJECTED, otherPendingRequest.getStatus());
        assertNotNull(otherPendingRequest.getResponseDate());

        verify(animalRepository, times(1)).save(animal);
        verify(adoptionRequestRepository, times(2)).save(any(AdoptionRequest.class));
    }
    @Test
    void shouldThrowExceptionWhenNonOwnerTriesToRejectRequest() throws Exception {
        User owner = new User();
        setId(owner, 1L);
        owner.setName("Carlos Owner");

        User adopter = new User();
        setId(adopter, 2L);
        adopter.setName("Ana Adopter");

        User anotherUser = new User();
        setId(anotherUser, 3L);
        anotherUser.setName("Bruno Adopter");

        Animal animal = new Animal();
        setId(animal, 10L);
        animal.setAnimalName("Bob");
        animal.setStatus("AVAILABLE");
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        setId(request, 100L);
        request.setAnimal(animal);
        request.setUser(adopter);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(adoptionRequestRepository.findById(100L)).thenReturn(Optional.of(request));

        assertThrows(
                OnlyOwnerCanManageAdoptionRequestException.class,
                () -> adoptionRequestService.rejectRequest(100L, 3L)
        );
    }
    @Test
    void shouldThrowExceptionWhenTryingToRejectRequestThatIsNotPending() throws Exception {
        User owner = new User();
        setId(owner, 1L);
        owner.setName("Carlos Owner");

        User adopter = new User();
        setId(adopter, 2L);
        adopter.setName("Ana Adopter");

        Animal animal = new Animal();
        setId(animal, 10L);
        animal.setAnimalName("Bob");
        animal.setStatus("AVAILABLE");
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        setId(request, 100L);
        request.setAnimal(animal);
        request.setUser(adopter);
        request.setStatus(AdoptionRequestStatus.REJECTED);

        when(adoptionRequestRepository.findById(100L)).thenReturn(Optional.of(request));

        assertThrows(
                AdoptionRequestNotPendingException.class,
                () -> adoptionRequestService.rejectRequest(100L, 1L)
        );
    }
    @Test
    void shouldRejectRequestAndKeepAnimalStatusUnchanged() throws Exception {
        User owner = new User();
        setId(owner, 1L);
        owner.setName("Carlos Owner");

        User adopter = new User();
        setId(adopter, 2L);
        adopter.setName("Ana Adopter");

        Animal animal = new Animal();
        setId(animal, 10L);
        animal.setAnimalName("Bob");
        animal.setStatus("AVAILABLE");
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        setId(request, 100L);
        request.setAnimal(animal);
        request.setUser(adopter);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(adoptionRequestRepository.findById(100L)).thenReturn(Optional.of(request));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdoptionRequest result = adoptionRequestService.rejectRequest(100L, 1L);

        assertEquals(AdoptionRequestStatus.REJECTED, result.getStatus());
        assertNotNull(result.getResponseDate());

        assertEquals("AVAILABLE", animal.getStatus());

        verify(adoptionRequestRepository, times(1)).save(request);
    }

    @Test
    void shouldSaveAdoptionRequestWithPendingStatusAndGeneratedRequestDate() throws Exception {
        User owner = new User();
        setId(owner, 1L);
        owner.setName("Carlos Owner");

        User adopter = new User();
        setId(adopter, 2L);
        adopter.setName("Ana Adopter");

        Animal animal = new Animal();
        setId(animal, 10L);
        animal.setAnimalName("Bob");
        animal.setStatus("AVAILABLE");
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setAnimal(animal);
        request.setUser(adopter);
        request.setMessage("I want to adopt Bob");

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adopter));
        when(adoptionRequestRepository.existsByAnimal_IdAndUser_IdAndStatus(
                10L, 2L, AdoptionRequestStatus.PENDING
        )).thenReturn(false);
        when(adoptionRequestRepository.save(any(AdoptionRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdoptionRequest result = adoptionRequestService.save(request);

        assertEquals(AdoptionRequestStatus.PENDING, result.getStatus());
        assertNotNull(result.getRequestDate());
        assertEquals(null, result.getResponseDate());

        assertEquals(animal, result.getAnimal());
        assertEquals(adopter, result.getUser());
        assertEquals("I want to adopt Bob", result.getMessage());

        verify(adoptionRequestRepository, times(1)).save(request);
    }
}