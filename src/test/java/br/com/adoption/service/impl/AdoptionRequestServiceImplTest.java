package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateAdoptionRequest;
import br.com.adoption.dto.request.UpdateRequestStatusRequest;
import br.com.adoption.dto.response.AdoptionRequestResponse;
import br.com.adoption.entity.AdoptionRequest;
import br.com.adoption.entity.AdoptionRequestStatus;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.User;
import br.com.adoption.exception.AdoptionRequestNotPendingException;
import br.com.adoption.exception.AnimalNotAvailableException;
import br.com.adoption.exception.DuplicateAdoptionRequestException;
import br.com.adoption.exception.OnlyOwnerCanManageAdoptionRequestException;
import br.com.adoption.exception.OwnerCannotAdoptOwnAnimalException;
import br.com.adoption.repository.AdoptionRequestRepository;
import br.com.adoption.repository.AnimalRepository;
import br.com.adoption.repository.UserRepository;
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
    void shouldReturnAllRequestsOrderedById() {
        AdoptionRequest request1 = new AdoptionRequest();
        request1.setMessage("Request 1");
        request1.setStatus(AdoptionRequestStatus.PENDING);

        AdoptionRequest request2 = new AdoptionRequest();
        request2.setMessage("Request 2");
        request2.setStatus(AdoptionRequestStatus.APPROVED);

        when(adoptionRequestRepository.findAll(Sort.by("id"))).thenReturn(List.of(request1, request2));

        List<AdoptionRequestResponse> result = adoptionRequestService.getAllRequests();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Request 1", result.get(0).getMessage());
        assertEquals("Request 2", result.get(1).getMessage());

        verify(adoptionRequestRepository, times(1)).findAll(Sort.by("id"));
    }

    @Test
    void shouldSaveValidRequestWithPendingStatus() {
        CreateAdoptionRequest requestDto = new CreateAdoptionRequest();
        requestDto.setMessage("I want to adopt Nina");
        requestDto.setAnimalId(10L);
        requestDto.setUserId(2L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);

        Animal animal = spy(new Animal());
        doReturn(10L).when(animal).getId();
        animal.setStatus("AVAILABLE");
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findById(2L)).thenReturn(Optional.of(requester));
        when(adoptionRequestRepository.existsByAnimal_IdAndUser_IdAndStatus(10L, 2L, AdoptionRequestStatus.PENDING))
                .thenReturn(false);
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdoptionRequestResponse result = adoptionRequestService.save(requestDto);

        ArgumentCaptor<AdoptionRequest> captor = ArgumentCaptor.forClass(AdoptionRequest.class);
        verify(adoptionRequestRepository, times(1)).save(captor.capture());

        AdoptionRequest savedRequest = captor.getValue();

        assertNotNull(result);
        assertEquals("I want to adopt Nina", result.getMessage());
        assertEquals(AdoptionRequestStatus.PENDING, result.getStatus());
        assertEquals(10L, result.getAnimalId());
        assertEquals(2L, result.getUserId());
        assertNull(result.getResponseDate());
        assertNotNull(result.getRequestDate());

        assertEquals("I want to adopt Nina", savedRequest.getMessage());
        assertEquals(AdoptionRequestStatus.PENDING, savedRequest.getStatus());
        assertNotNull(savedRequest.getRequestDate());
        assertEquals(animal, savedRequest.getAnimal());
        assertEquals(requester, savedRequest.getUser());
    }

    @Test
    void shouldThrowExceptionWhenOwnerTriesToAdoptOwnAnimal() {
        CreateAdoptionRequest requestDto = new CreateAdoptionRequest();
        requestDto.setAnimalId(10L);
        requestDto.setUserId(1L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = new Animal();
        animal.setStatus("AVAILABLE");
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        OwnerCannotAdoptOwnAnimalException exception = assertThrows(
                OwnerCannotAdoptOwnAnimalException.class,
                () -> adoptionRequestService.save(requestDto)
        );

        assertEquals("The owner cannot create an adoption request for their own animal", exception.getMessage());
        verify(adoptionRequestRepository, never()).save(any(AdoptionRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenDuplicatePendingRequestExists() {
        CreateAdoptionRequest requestDto = new CreateAdoptionRequest();
        requestDto.setAnimalId(10L);
        requestDto.setUserId(2L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);

        Animal animal = spy(new Animal());
        doReturn(10L).when(animal).getId();
        animal.setStatus("AVAILABLE");
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findById(2L)).thenReturn(Optional.of(requester));
        when(adoptionRequestRepository.existsByAnimal_IdAndUser_IdAndStatus(10L, 2L, AdoptionRequestStatus.PENDING))
                .thenReturn(true);

        DuplicateAdoptionRequestException exception = assertThrows(
                DuplicateAdoptionRequestException.class,
                () -> adoptionRequestService.save(requestDto)
        );

        assertEquals("User already has a pending request for this animal", exception.getMessage());
        verify(adoptionRequestRepository, never()).save(any(AdoptionRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenAnimalIsNotAvailable() {
        CreateAdoptionRequest requestDto = new CreateAdoptionRequest();
        requestDto.setAnimalId(10L);
        requestDto.setUserId(2L);

        Animal animal = new Animal();
        animal.setStatus("ADOPTED");

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));

        AnimalNotAvailableException exception = assertThrows(
                AnimalNotAvailableException.class,
                () -> adoptionRequestService.save(requestDto)
        );

        assertEquals("Animal is not available for adoption", exception.getMessage());
        verify(userRepository, never()).findById(anyLong());
        verify(adoptionRequestRepository, never()).save(any(AdoptionRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenNonOwnerTriesToApprove() {
        UpdateRequestStatusRequest requestDto = new UpdateRequestStatusRequest();
        requestDto.setUserId(99L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = new Animal();
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setAnimal(animal);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));

        OnlyOwnerCanManageAdoptionRequestException exception = assertThrows(
                OnlyOwnerCanManageAdoptionRequestException.class,
                () -> adoptionRequestService.approveRequest(20L, requestDto)
        );

        assertEquals("Only the animal owner can approve this request", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenApprovingNonPendingRequest() {
        UpdateRequestStatusRequest requestDto = new UpdateRequestStatusRequest();
        requestDto.setUserId(1L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = new Animal();
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setAnimal(animal);
        request.setStatus(AdoptionRequestStatus.REJECTED);

        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));

        AdoptionRequestNotPendingException exception = assertThrows(
                AdoptionRequestNotPendingException.class,
                () -> adoptionRequestService.approveRequest(20L, requestDto)
        );

        assertEquals("Only pending requests can be approved", exception.getMessage());
    }

    @Test
    void shouldApproveRequestAndRejectOtherPendingRequests() {
        UpdateRequestStatusRequest requestDto = new UpdateRequestStatusRequest();
        requestDto.setUserId(1L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = spy(new Animal());
        doReturn(10L).when(animal).getId();
        animal.setStatus("AVAILABLE");
        animal.setUser(owner);

        AdoptionRequest approvedRequest = spy(new AdoptionRequest());
        doReturn(20L).when(approvedRequest).getId();
        approvedRequest.setAnimal(animal);
        approvedRequest.setStatus(AdoptionRequestStatus.PENDING);

        AdoptionRequest pendingRequest = spy(new AdoptionRequest());
        doReturn(21L).when(pendingRequest).getId();
        pendingRequest.setAnimal(animal);
        pendingRequest.setStatus(AdoptionRequestStatus.PENDING);

        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(approvedRequest));
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(adoptionRequestRepository.findByAnimal_IdAndStatus(10L, AdoptionRequestStatus.PENDING))
                .thenReturn(List.of(approvedRequest, pendingRequest));

        AdoptionRequestResponse result = adoptionRequestService.approveRequest(20L, requestDto);

        assertNotNull(result);
        assertEquals(AdoptionRequestStatus.APPROVED, result.getStatus());
        assertNotNull(result.getResponseDate());
        assertEquals("ADOPTED", animal.getStatus());
        assertEquals(AdoptionRequestStatus.REJECTED, pendingRequest.getStatus());
        assertNotNull(pendingRequest.getResponseDate());

        verify(animalRepository, times(1)).save(animal);
        verify(adoptionRequestRepository, atLeast(2)).save(any(AdoptionRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenNonOwnerTriesToReject() {
        UpdateRequestStatusRequest requestDto = new UpdateRequestStatusRequest();
        requestDto.setUserId(99L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = new Animal();
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setAnimal(animal);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));

        OnlyOwnerCanManageAdoptionRequestException exception = assertThrows(
                OnlyOwnerCanManageAdoptionRequestException.class,
                () -> adoptionRequestService.rejectRequest(20L, requestDto)
        );

        assertEquals("Only the animal owner can reject this request", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRejectingNonPendingRequest() {
        UpdateRequestStatusRequest requestDto = new UpdateRequestStatusRequest();
        requestDto.setUserId(1L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = new Animal();
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setAnimal(animal);
        request.setStatus(AdoptionRequestStatus.APPROVED);

        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));

        AdoptionRequestNotPendingException exception = assertThrows(
                AdoptionRequestNotPendingException.class,
                () -> adoptionRequestService.rejectRequest(20L, requestDto)
        );

        assertEquals("Only pending requests can be rejected", exception.getMessage());
    }
    @Test
    void shouldRejectPendingRequest() {
        UpdateRequestStatusRequest requestDto = new UpdateRequestStatusRequest();
        requestDto.setUserId(1L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = new Animal();
        animal.setUser(owner);

        AdoptionRequest request = spy(new AdoptionRequest());
        doReturn(20L).when(request).getId();
        request.setAnimal(animal);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdoptionRequestResponse result = adoptionRequestService.rejectRequest(20L, requestDto);

        assertNotNull(result);
        assertEquals(AdoptionRequestStatus.REJECTED, result.getStatus());
        assertNotNull(result.getResponseDate());
        verify(adoptionRequestRepository, times(1)).save(request);
    }
}