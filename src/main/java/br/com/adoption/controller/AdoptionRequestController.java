package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAdoptionRequest;
import br.com.adoption.dto.request.UpdateRequestStatusRequest;
import br.com.adoption.dto.response.AdoptionRequestResponse;
import br.com.adoption.service.AdoptionRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adoption-requests")
public class AdoptionRequestController {

    private final AdoptionRequestService adoptionRequestService;

    public AdoptionRequestController(AdoptionRequestService adoptionRequestService) {
        this.adoptionRequestService = adoptionRequestService;
    }

    @GetMapping
    public List<AdoptionRequestResponse> getAllRequests() {
        return adoptionRequestService.getAllRequests();
    }

    @PostMapping
    public AdoptionRequestResponse createRequest(@Valid @RequestBody CreateAdoptionRequest request) {
        return adoptionRequestService.save(request);
    }

    @PatchMapping("/{id}/approve")
    public AdoptionRequestResponse approveRequest(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateRequestStatusRequest request) {
        return adoptionRequestService.approveRequest(id, request);
    }

    @PatchMapping("/{id}/reject")
    public AdoptionRequestResponse rejectRequest(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateRequestStatusRequest request) {
        return adoptionRequestService.rejectRequest(id, request);
    }
}