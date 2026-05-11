package br.com.adoption.service;

import br.com.adoption.dto.request.CreateAdoptionRequestMessageRequest;
import br.com.adoption.dto.response.AdoptionRequestMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdoptionRequestMessageService {
    Page<AdoptionRequestMessageResponse> getMessages(Long adoptionRequestId, Pageable pageable, String userEmail);
    AdoptionRequestMessageResponse sendMessage(Long adoptionRequestId, CreateAdoptionRequestMessageRequest request, String userEmail);
}
