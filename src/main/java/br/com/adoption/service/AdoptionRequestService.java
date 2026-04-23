package br.com.adoption.service;

import br.com.adoption.dto.request.CreateAdoptionRequest;
import br.com.adoption.dto.response.AdoptionRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdoptionRequestService {
    List<AdoptionRequestResponse> getAllRequests();
    Page<AdoptionRequestResponse> getAllRequests(Pageable pageable);
    AdoptionRequestResponse getById(Long requestId, String userEmail);
    AdoptionRequestResponse save(CreateAdoptionRequest request, String userEmail);
    AdoptionRequestResponse approveRequest(Long requestId, String userEmail);
    AdoptionRequestResponse rejectRequest(Long requestId, String userEmail);
    AdoptionRequestResponse cancelRequest(Long requestId, String userEmail);
}
