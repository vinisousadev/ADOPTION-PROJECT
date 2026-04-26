package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateAdoptionRequest;
import br.com.adoption.dto.response.AdoptionRequestResponse;
import br.com.adoption.entity.AdoptionRequest;
import br.com.adoption.entity.AdoptionRequestStatus;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.AnimalStatus;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

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
    void shouldReturnFilteredRequests() {
        AdoptionRequest request = new AdoptionRequest();
        request.setMessage("Filtered request");
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(adoptionRequestRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(request)));

        Page<AdoptionRequestResponse> result = adoptionRequestService.getAllRequests(
                PageRequest.of(0, 10),
                AdoptionRequestStatus.PENDING,
                10L,
                2L
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Filtered request", result.getContent().getFirst().getMessage());
        assertEquals(AdoptionRequestStatus.PENDING, result.getContent().getFirst().getStatus());

        verify(adoptionRequestRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void shouldReturnRequestByIdWhenUserIsRequester() {
        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);
        when(requester.getUserType()).thenReturn(br.com.adoption.entity.UserType.COMMON);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = new Animal();
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setUser(requester);
        request.setAnimal(animal);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(userRepository.findByEmail("requester@email.com")).thenReturn(Optional.of(requester));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));

        AdoptionRequestResponse result = adoptionRequestService.getById(20L, "requester@email.com");

        assertNotNull(result);
        assertEquals(AdoptionRequestStatus.PENDING, result.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenUserCannotAccessRequestById() {
        User intruder = mock(User.class);
        when(intruder.getId()).thenReturn(99L);
        when(intruder.getUserType()).thenReturn(br.com.adoption.entity.UserType.COMMON);

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = new Animal();
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setUser(requester);
        request.setAnimal(animal);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(userRepository.findByEmail("intruder@email.com")).thenReturn(Optional.of(intruder));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));

        OnlyOwnerCanManageAdoptionRequestException exception = assertThrows(
                OnlyOwnerCanManageAdoptionRequestException.class,
                () -> adoptionRequestService.getById(20L, "intruder@email.com")
        );

        assertEquals(
                "Only requester, animal owner or admin can access this adoption request",
                exception.getMessage()
        );
    }

    @Test
    void shouldSaveValidRequestWithPendingStatus() {
        CreateAdoptionRequest requestDto = new CreateAdoptionRequest();
        requestDto.setMessage("I want to adopt Nina");
        requestDto.setAnimalId(10L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);

        Animal animal = spy(new Animal());
        doReturn(10L).when(animal).getId();
        animal.setStatus(AnimalStatus.AVAILABLE);
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findByEmail("requester@email.com")).thenReturn(Optional.of(requester));
        when(adoptionRequestRepository.existsByAnimal_IdAndUser_IdAndStatus(10L, 2L, AdoptionRequestStatus.PENDING))
                .thenReturn(false);
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdoptionRequestResponse result = adoptionRequestService.save(requestDto, "requester@email.com");

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

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = new Animal();
        animal.setStatus(AnimalStatus.AVAILABLE);
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(owner));

        OwnerCannotAdoptOwnAnimalException exception = assertThrows(
                OwnerCannotAdoptOwnAnimalException.class,
                () -> adoptionRequestService.save(requestDto, "owner@email.com")
        );

        assertEquals("The owner cannot create an adoption request for their own animal", exception.getMessage());
        verify(adoptionRequestRepository, never()).save(any(AdoptionRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenDuplicatePendingRequestExists() {
        CreateAdoptionRequest requestDto = new CreateAdoptionRequest();
        requestDto.setAnimalId(10L);

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);

        Animal animal = spy(new Animal());
        doReturn(10L).when(animal).getId();
        animal.setStatus(AnimalStatus.AVAILABLE);
        animal.setUser(owner);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));
        when(userRepository.findByEmail("requester@email.com")).thenReturn(Optional.of(requester));
        when(adoptionRequestRepository.existsByAnimal_IdAndUser_IdAndStatus(10L, 2L, AdoptionRequestStatus.PENDING))
                .thenReturn(true);

        DuplicateAdoptionRequestException exception = assertThrows(
                DuplicateAdoptionRequestException.class,
                () -> adoptionRequestService.save(requestDto, "requester@email.com")
        );

        assertEquals("User already has a pending request for this animal", exception.getMessage());
        verify(adoptionRequestRepository, never()).save(any(AdoptionRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenAnimalIsNotAvailable() {
        CreateAdoptionRequest requestDto = new CreateAdoptionRequest();
        requestDto.setAnimalId(10L);

        Animal animal = new Animal();
        animal.setStatus(AnimalStatus.ADOPTED);

        when(animalRepository.findById(10L)).thenReturn(Optional.of(animal));

        AnimalNotAvailableException exception = assertThrows(
                AnimalNotAvailableException.class,
                () -> adoptionRequestService.save(requestDto, "requester@email.com")
        );

        assertEquals("Animal is not available for adoption", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
        verify(adoptionRequestRepository, never()).save(any(AdoptionRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenNonOwnerTriesToApprove() {
        String authenticatedEmail = "intruso@email.com";

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User intruder = mock(User.class);
        when(intruder.getId()).thenReturn(99L);

        Animal animal = new Animal();
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setAnimal(animal);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(intruder));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));

        OnlyOwnerCanManageAdoptionRequestException exception = assertThrows(
                OnlyOwnerCanManageAdoptionRequestException.class,
                () -> adoptionRequestService.approveRequest(20L, authenticatedEmail)
        );

        assertEquals("Only the animal owner or admin can approve this request", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenApprovingNonPendingRequest() {
        String authenticatedEmail = "owner@email.com";

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = new Animal();
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setAnimal(animal);
        request.setStatus(AdoptionRequestStatus.REJECTED);

        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(owner));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));

        AdoptionRequestNotPendingException exception = assertThrows(
                AdoptionRequestNotPendingException.class,
                () -> adoptionRequestService.approveRequest(20L, authenticatedEmail)
        );

        assertEquals("Only pending requests can be approved", exception.getMessage());
    }

    @Test
    void shouldApproveRequestAndRejectOtherPendingRequests() {
        String authenticatedEmail = "owner@email.com";

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);

        User otherRequester = mock(User.class);

        Animal animal = spy(new Animal());
        doReturn(10L).when(animal).getId();
        animal.setStatus(AnimalStatus.AVAILABLE);
        animal.setUser(owner);

        AdoptionRequest approvedRequest = spy(new AdoptionRequest());
        doReturn(20L).when(approvedRequest).getId();
        approvedRequest.setAnimal(animal);
        approvedRequest.setUser(requester);
        approvedRequest.setStatus(AdoptionRequestStatus.PENDING);

        AdoptionRequest pendingRequest = spy(new AdoptionRequest());
        doReturn(21L).when(pendingRequest).getId();
        pendingRequest.setAnimal(animal);
        pendingRequest.setUser(otherRequester);
        pendingRequest.setStatus(AdoptionRequestStatus.PENDING);

        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(owner));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(approvedRequest));
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(adoptionRequestRepository.findByAnimal_IdAndStatus(10L, AdoptionRequestStatus.PENDING))
                .thenReturn(List.of(approvedRequest, pendingRequest));

        AdoptionRequestResponse result = adoptionRequestService.approveRequest(20L, authenticatedEmail);

        assertNotNull(result);
        assertEquals(AdoptionRequestStatus.APPROVED, result.getStatus());
        assertEquals(10L, result.getAnimalId());
        assertEquals(2L, result.getUserId());
        assertNotNull(result.getResponseDate());

        assertEquals(AnimalStatus.ADOPTED, animal.getStatus());
        assertEquals(AdoptionRequestStatus.REJECTED, pendingRequest.getStatus());
        assertNotNull(pendingRequest.getResponseDate());

        verify(animalRepository, times(1)).save(animal);
        verify(adoptionRequestRepository, atLeast(2)).save(any(AdoptionRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenNonOwnerTriesToReject() {
        String authenticatedEmail = "intruso@email.com";

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User intruder = mock(User.class);
        when(intruder.getId()).thenReturn(99L);

        Animal animal = new Animal();
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setAnimal(animal);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(intruder));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));

        OnlyOwnerCanManageAdoptionRequestException exception = assertThrows(
                OnlyOwnerCanManageAdoptionRequestException.class,
                () -> adoptionRequestService.rejectRequest(20L, authenticatedEmail)
        );

        assertEquals("Only the animal owner or admin can reject this request", exception.getMessage());
    }

    @Test
    void shouldAllowAdminToApproveRequest() {
        String authenticatedEmail = "admin@email.com";

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);

        User admin = mock(User.class);
        when(admin.getId()).thenReturn(99L);
        when(admin.getUserType()).thenReturn(br.com.adoption.entity.UserType.ADMIN);

        Animal animal = spy(new Animal());
        doReturn(10L).when(animal).getId();
        animal.setUser(owner);
        animal.setStatus(AnimalStatus.AVAILABLE);

        AdoptionRequest request = spy(new AdoptionRequest());
        doReturn(20L).when(request).getId();
        request.setAnimal(animal);
        request.setUser(requester);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(admin));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(adoptionRequestRepository.findByAnimal_IdAndStatus(10L, AdoptionRequestStatus.PENDING))
                .thenReturn(List.of(request));

        AdoptionRequestResponse result = adoptionRequestService.approveRequest(20L, authenticatedEmail);

        assertNotNull(result);
        assertEquals(AdoptionRequestStatus.APPROVED, result.getStatus());
    }

    @Test
    void shouldAllowAdminToRejectRequest() {
        String authenticatedEmail = "admin@email.com";

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);

        User admin = mock(User.class);
        when(admin.getId()).thenReturn(99L);
        when(admin.getUserType()).thenReturn(br.com.adoption.entity.UserType.ADMIN);

        Animal animal = spy(new Animal());
        doReturn(10L).when(animal).getId();
        animal.setUser(owner);

        AdoptionRequest request = spy(new AdoptionRequest());
        doReturn(20L).when(request).getId();
        request.setAnimal(animal);
        request.setUser(requester);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(admin));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdoptionRequestResponse result = adoptionRequestService.rejectRequest(20L, authenticatedEmail);

        assertNotNull(result);
        assertEquals(AdoptionRequestStatus.REJECTED, result.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenRejectingNonPendingRequest() {
        String authenticatedEmail = "owner@email.com";

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        Animal animal = new Animal();
        animal.setUser(owner);

        AdoptionRequest request = new AdoptionRequest();
        request.setAnimal(animal);
        request.setStatus(AdoptionRequestStatus.APPROVED);

        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(owner));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));

        AdoptionRequestNotPendingException exception = assertThrows(
                AdoptionRequestNotPendingException.class,
                () -> adoptionRequestService.rejectRequest(20L, authenticatedEmail)
        );

        assertEquals("Only pending requests can be rejected", exception.getMessage());
    }

    @Test
    void shouldRejectPendingRequest() {
        String authenticatedEmail = "owner@email.com";

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);

        Animal animal = spy(new Animal());
        doReturn(10L).when(animal).getId();
        animal.setUser(owner);

        AdoptionRequest request = spy(new AdoptionRequest());
        doReturn(20L).when(request).getId();
        request.setAnimal(animal);
        request.setUser(requester);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(owner));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdoptionRequestResponse result = adoptionRequestService.rejectRequest(20L, authenticatedEmail);

        assertNotNull(result);
        assertEquals(AdoptionRequestStatus.REJECTED, result.getStatus());
        assertEquals(10L, result.getAnimalId());
        assertEquals(2L, result.getUserId());
        assertNotNull(result.getResponseDate());

        verify(adoptionRequestRepository, times(1)).save(request);
    }

    @Test
    void shouldCancelPendingRequestWhenRequester() {
        String authenticatedEmail = "requester@email.com";

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);
        when(requester.getUserType()).thenReturn(br.com.adoption.entity.UserType.COMMON);

        Animal animal = new Animal();

        AdoptionRequest request = spy(new AdoptionRequest());
        doReturn(20L).when(request).getId();
        request.setAnimal(animal);
        request.setUser(requester);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(requester));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdoptionRequestResponse result = adoptionRequestService.cancelRequest(20L, authenticatedEmail);

        assertNotNull(result);
        assertEquals(AdoptionRequestStatus.CANCELLED, result.getStatus());
        assertNotNull(result.getResponseDate());
    }

    @Test
    void shouldAllowAdminToCancelPendingRequest() {
        String authenticatedEmail = "admin@email.com";

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);

        User admin = mock(User.class);
        when(admin.getId()).thenReturn(99L);
        when(admin.getUserType()).thenReturn(br.com.adoption.entity.UserType.ADMIN);

        AdoptionRequest request = new AdoptionRequest();
        request.setUser(requester);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(admin));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdoptionRequestResponse result = adoptionRequestService.cancelRequest(20L, authenticatedEmail);

        assertNotNull(result);
        assertEquals(AdoptionRequestStatus.CANCELLED, result.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenNonOwnerNonAdminTriesToCancel() {
        String authenticatedEmail = "intruder@email.com";

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);

        User intruder = mock(User.class);
        when(intruder.getId()).thenReturn(88L);
        when(intruder.getUserType()).thenReturn(br.com.adoption.entity.UserType.COMMON);

        AdoptionRequest request = new AdoptionRequest();
        request.setUser(requester);
        request.setStatus(AdoptionRequestStatus.PENDING);

        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(intruder));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));

        OnlyOwnerCanManageAdoptionRequestException exception = assertThrows(
                OnlyOwnerCanManageAdoptionRequestException.class,
                () -> adoptionRequestService.cancelRequest(20L, authenticatedEmail)
        );

        assertEquals("Only the request owner or admin can cancel this request", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCancellingNonPendingRequest() {
        String authenticatedEmail = "requester@email.com";

        User requester = mock(User.class);
        when(requester.getId()).thenReturn(2L);
        when(requester.getUserType()).thenReturn(br.com.adoption.entity.UserType.COMMON);

        AdoptionRequest request = new AdoptionRequest();
        request.setUser(requester);
        request.setStatus(AdoptionRequestStatus.APPROVED);

        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(requester));
        when(adoptionRequestRepository.findById(20L)).thenReturn(Optional.of(request));

        AdoptionRequestNotPendingException exception = assertThrows(
                AdoptionRequestNotPendingException.class,
                () -> adoptionRequestService.cancelRequest(20L, authenticatedEmail)
        );

        assertEquals("Only pending requests can be canceled", exception.getMessage());
    }
}
