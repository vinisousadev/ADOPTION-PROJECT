package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateAdoptionRequestMessageRequest;
import br.com.adoption.dto.response.AdoptionRequestMessageResponse;
import br.com.adoption.entity.*;
import br.com.adoption.exception.OnlyOwnerCanManageAdoptionRequestException;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.repository.AdoptionRequestMessageRepository;
import br.com.adoption.repository.AdoptionRequestRepository;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.AdoptionRequestMessageService;
import br.com.adoption.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class AdoptionRequestMessageServiceImpl implements AdoptionRequestMessageService {

    private final AdoptionRequestMessageRepository messageRepository;
    private final AdoptionRequestRepository adoptionRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AdoptionRequestMessageServiceImpl(
            AdoptionRequestMessageRepository messageRepository,
            AdoptionRequestRepository adoptionRequestRepository,
            UserRepository userRepository,
            NotificationService notificationService
    ) {
        this.messageRepository = messageRepository;
        this.adoptionRequestRepository = adoptionRequestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Page<AdoptionRequestMessageResponse> getMessages(Long adoptionRequestId, Pageable pageable, String userEmail) {
        User authenticatedUser = getAuthenticatedUser(userEmail);
        AdoptionRequest adoptionRequest = getAdoptionRequest(adoptionRequestId);

        validateParticipant(adoptionRequest, authenticatedUser);

        return messageRepository.findByAdoptionRequest_Id(adoptionRequestId, pageable)
                .map(this::toResponse);
    }

    @Override
    public AdoptionRequestMessageResponse sendMessage(
            Long adoptionRequestId,
            CreateAdoptionRequestMessageRequest request,
            String userEmail
    ) {
        User authenticatedUser = getAuthenticatedUser(userEmail);
        AdoptionRequest adoptionRequest = getAdoptionRequest(adoptionRequestId);

        validateParticipant(adoptionRequest, authenticatedUser);
        validateChatOpen(adoptionRequest);

        AdoptionRequestMessage message = new AdoptionRequestMessage();
        message.setAdoptionRequest(adoptionRequest);
        message.setSender(authenticatedUser);
        message.setMessage(request.getMessage().trim());
        message.setCreatedAt(OffsetDateTime.now());

        AdoptionRequestMessage savedMessage = messageRepository.save(message);

        User recipient = getRecipient(adoptionRequest, authenticatedUser);

        if (recipient != null) {
            notificationService.notify(
                    recipient,
                    "Nova mensagem sobre adocao",
                    authenticatedUser.getName() + " enviou uma mensagem sobre " + adoptionRequest.getAnimal().getAnimalName() + ".",
                    "ADOPTION_REQUEST_MESSAGE_CREATED",
                    "ADOPTION_REQUEST",
                    adoptionRequest.getId(),
                    "/adoption-requests/" + adoptionRequest.getId()
            );
        }

        return toResponse(savedMessage);
    }

    private User getAuthenticatedUser(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private AdoptionRequest getAdoptionRequest(Long adoptionRequestId) {
        return adoptionRequestRepository.findById(adoptionRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption request not found"));
    }

    private void validateParticipant(AdoptionRequest request, User user) {
        boolean isAdmin = user.getUserType() == UserType.ADMIN;

        boolean isRequester = request.getUser() != null
                && request.getUser().getId().equals(user.getId());

        boolean isAnimalOwner = request.getAnimal() != null
                && request.getAnimal().getUser() != null
                && request.getAnimal().getUser().getId().equals(user.getId());

        if (!isAdmin && !isRequester && !isAnimalOwner) {
            throw new OnlyOwnerCanManageAdoptionRequestException(
                    "Only requester, animal owner or admin can access this chat"
            );
        }
    }

    private void validateChatOpen(AdoptionRequest request) {
        if (request.getStatus() != AdoptionRequestStatus.PENDING
                && request.getStatus() != AdoptionRequestStatus.APPROVED) {
            throw new OnlyOwnerCanManageAdoptionRequestException(
                    "Chat is closed for this adoption request"
            );
        }
    }

    private User getRecipient(AdoptionRequest request, User sender) {
        User requester = request.getUser();
        User animalOwner = request.getAnimal().getUser();

        if (requester != null && requester.getId().equals(sender.getId())) {
            return animalOwner;
        }

        return requester;
    }

    private AdoptionRequestMessageResponse toResponse(AdoptionRequestMessage message) {
        AdoptionRequestMessageResponse response = new AdoptionRequestMessageResponse();
        response.setId(message.getId());
        response.setAdoptionRequestId(message.getAdoptionRequest().getId());
        response.setSenderId(message.getSender().getId());
        response.setSenderName(message.getSender().getName());
        response.setSenderProfilePhotoUrl(message.getSender().getProfilePhotoUrl());
        response.setMessage(message.getMessage());
        response.setCreatedAt(message.getCreatedAt());
        response.setReadAt(message.getReadAt());
        return response;
    }
}
