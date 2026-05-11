package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAdoptionRequestMessageRequest;
import br.com.adoption.dto.response.AdoptionRequestMessageResponse;
import br.com.adoption.service.AdoptionRequestMessageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/adoption-requests/{adoptionRequestId}/messages")
public class AdoptionRequestMessageController {

    private final AdoptionRequestMessageService messageService;

    public AdoptionRequestMessageController(AdoptionRequestMessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public PagedModel<AdoptionRequestMessageResponse> getMessages(
            @PathVariable Long adoptionRequestId,
            @ParameterObject
            @PageableDefault(size = 30, sort = "createdAt") Pageable pageable,
            Authentication authentication
    ) {
        return new PagedModel<>(
                messageService.getMessages(adoptionRequestId, pageable, authentication.getName())
        );
    }

    @PostMapping
    public AdoptionRequestMessageResponse sendMessage(
            @PathVariable Long adoptionRequestId,
            @Valid @RequestBody CreateAdoptionRequestMessageRequest request,
            Authentication authentication
    ) {
        return messageService.sendMessage(adoptionRequestId, request, authentication.getName());
    }
}
