package br.com.adoption.service;

import br.com.adoption.entity.AdoptionRequest;

import java.util.List;

public interface AdoptionRequestService {
    List<AdoptionRequest> getAllRequests();
    AdoptionRequest save(AdoptionRequest adoptionRequest);
    AdoptionRequest approveRequest(Long requestId, Long userId);
    AdoptionRequest rejectRequest(Long requestId, Long userId);
}