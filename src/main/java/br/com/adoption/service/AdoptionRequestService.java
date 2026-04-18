package br.com.adoption.service;

import br.com.adoption.dto.request.CreateAdoptionRequest;
import br.com.adoption.dto.request.UpdateRequestStatusRequest;
import br.com.adoption.dto.response.AdoptionRequestResponse;

import java.util.List;

public interface AdoptionRequestService {
    List<AdoptionRequestResponse> getAllRequests();
    AdoptionRequestResponse save(CreateAdoptionRequest request);
    AdoptionRequestResponse approveRequest(Long requestId, UpdateRequestStatusRequest request);
    AdoptionRequestResponse rejectRequest(Long requestId, UpdateRequestStatusRequest request);
}