package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateAdoptionRequest;
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
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.mapper.AdoptionRequestMapper;
import br.com.adoption.repository.AdoptionRequestRepository;
import br.com.adoption.repository.AnimalRepository;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.AdoptionRequestService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdoptionRequestServiceImpl implements AdoptionRequestService {

    private final AdoptionRequestRepository adoptionRequestRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;

    public AdoptionRequestServiceImpl(AdoptionRequestRepository adoptionRequestRepository,
                                      AnimalRepository animalRepository,
                                      UserRepository userRepository) {
        this.adoptionRequestRepository = adoptionRequestRepository;
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AdoptionRequestResponse> getAllRequests() {
        return AdoptionRequestMapper.toResponseList(adoptionRequestRepository.findAll(Sort.by("id")));
    }

    @Override
    public AdoptionRequestResponse save(CreateAdoptionRequest request, String userEmail) {
        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

        if (!"AVAILABLE".equals(animal.getStatus())) {
            throw new AnimalNotAvailableException("Animal is not available for adoption");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (animal.getUser() != null && animal.getUser().getId().equals(user.getId())) {
            throw new OwnerCannotAdoptOwnAnimalException("The owner cannot create an adoption request for their own animal");
        }

        if (adoptionRequestRepository.existsByAnimal_IdAndUser_IdAndStatus(
                animal.getId(),
                user.getId(),
                AdoptionRequestStatus.PENDING)) {
            throw new DuplicateAdoptionRequestException("User already has a pending request for this animal");
        }

        AdoptionRequest adoptionRequest = AdoptionRequestMapper.toEntity(request);
        adoptionRequest.setAnimal(animal);
        adoptionRequest.setUser(user);
        adoptionRequest.setStatus(AdoptionRequestStatus.PENDING);
        adoptionRequest.setRequestDate(LocalDateTime.now());

        AdoptionRequest savedRequest = adoptionRequestRepository.save(adoptionRequest);
        return AdoptionRequestMapper.toResponse(savedRequest);
    }

    @Transactional
    @Override
    public AdoptionRequestResponse approveRequest(Long requestId, String userEmail) {
        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AdoptionRequest request = adoptionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption request not found"));

        if (!request.getAnimal().getUser().getId().equals(authenticatedUser.getId())) {
            throw new OnlyOwnerCanManageAdoptionRequestException("Only the animal owner can approve this request");
        }

        if (request.getStatus() != AdoptionRequestStatus.PENDING) {
            throw new AdoptionRequestNotPendingException("Only pending requests can be approved");
        }

        LocalDateTime now = LocalDateTime.now();

        request.setStatus(AdoptionRequestStatus.APPROVED);
        request.setResponseDate(now);

        Animal animal = request.getAnimal();
        animal.setStatus("ADOPTED");

        animalRepository.save(animal);
        AdoptionRequest approvedRequest = adoptionRequestRepository.save(request);

        List<AdoptionRequest> pendingRequests =
                adoptionRequestRepository.findByAnimal_IdAndStatus(animal.getId(), AdoptionRequestStatus.PENDING);

        for (AdoptionRequest pendingRequest : pendingRequests) {
            if (!pendingRequest.getId().equals(request.getId())) {
                pendingRequest.setStatus(AdoptionRequestStatus.REJECTED);
                pendingRequest.setResponseDate(now);
                adoptionRequestRepository.save(pendingRequest);
            }
        }

        return AdoptionRequestMapper.toResponse(approvedRequest);
    }

    @Override
    public AdoptionRequestResponse rejectRequest(Long requestId, String userEmail) {
        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AdoptionRequest request = adoptionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption request not found"));

        if (!request.getAnimal().getUser().getId().equals(authenticatedUser.getId())) {
            throw new OnlyOwnerCanManageAdoptionRequestException("Only the animal owner can reject this request");
        }

        if (request.getStatus() != AdoptionRequestStatus.PENDING) {
            throw new AdoptionRequestNotPendingException("Only pending requests can be rejected");
        }

        request.setStatus(AdoptionRequestStatus.REJECTED);
        request.setResponseDate(LocalDateTime.now());

        AdoptionRequest rejectedRequest = adoptionRequestRepository.save(request);
        return AdoptionRequestMapper.toResponse(rejectedRequest);
    }
}
