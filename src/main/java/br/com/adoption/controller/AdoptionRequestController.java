package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAdoptionRequest;
import br.com.adoption.dto.response.AdoptionRequestResponse;
import br.com.adoption.service.AdoptionRequestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
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
                                                  Authentication authentication) {
        return adoptionRequestService.approveRequest(id, authentication.getName());
    }

    @PatchMapping("/{id}/reject")
    public AdoptionRequestResponse rejectRequest(@PathVariable Long id,
                                                 Authentication authentication) {
        return adoptionRequestService.rejectRequest(id, authentication.getName());
    }
}