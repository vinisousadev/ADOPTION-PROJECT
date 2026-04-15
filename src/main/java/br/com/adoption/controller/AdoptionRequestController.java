package br.com.adoption.controller;

import br.com.adoption.entity.AdoptionRequest;
import br.com.adoption.service.AdoptionRequestService;
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
    public List<AdoptionRequest> getAllRequests() {
        return adoptionRequestService.getAllRequests();
    }

    @PostMapping
    public AdoptionRequest createRequest(@RequestBody AdoptionRequest adoptionRequest) {
        return adoptionRequestService.save(adoptionRequest);
    }
    @PatchMapping("/{id}/approve")
    public AdoptionRequest approveRequest(@PathVariable Long id,
                                          @RequestParam Long userId) {
        return adoptionRequestService.approveRequest(id, userId);
    }

    @PatchMapping("/{id}/reject")
    public AdoptionRequest rejectRequest(@PathVariable Long id,
                                         @RequestParam Long userId) {
        return adoptionRequestService.rejectRequest(id, userId);
    }
}