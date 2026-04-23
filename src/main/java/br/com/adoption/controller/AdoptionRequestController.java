package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAdoptionRequest;
import br.com.adoption.dto.response.AdoptionRequestResponse;
import br.com.adoption.service.AdoptionRequestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/adoption-requests")
public class AdoptionRequestController {

    private final AdoptionRequestService adoptionRequestService;

    public AdoptionRequestController(AdoptionRequestService adoptionRequestService) {
        this.adoptionRequestService = adoptionRequestService;
    }

    @GetMapping
    public PagedModel<AdoptionRequestResponse> getAllRequests(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return new PagedModel<>(adoptionRequestService.getAllRequests(pageable));
    }

    @GetMapping("/{id}")
    public AdoptionRequestResponse getRequestById(@PathVariable Long id,
                                                  Authentication authentication) {
        return adoptionRequestService.getById(id, authentication.getName());
    }

    @PostMapping
    public AdoptionRequestResponse createRequest(@Valid @RequestBody CreateAdoptionRequest request,
                                                 Authentication authentication) {
        return adoptionRequestService.save(request, authentication.getName());
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

    @PatchMapping("/{id}/cancel")
    public AdoptionRequestResponse cancelRequest(@PathVariable Long id,
                                                 Authentication authentication) {
        return adoptionRequestService.cancelRequest(id, authentication.getName());
    }
}
