package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAdoptionRequest;
import br.com.adoption.dto.response.AdoptionRequestResponse;
import br.com.adoption.entity.AdoptionRequestStatus;
import br.com.adoption.service.AdoptionRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Adoption Requests", description = "Adoption request endpoints")
@RestController
@RequestMapping("/adoption-requests")
public class AdoptionRequestController {

    private final AdoptionRequestService adoptionRequestService;

    public AdoptionRequestController(AdoptionRequestService adoptionRequestService) {
        this.adoptionRequestService = adoptionRequestService;
    }

    @GetMapping
    @Operation(
            summary = "List adoption requests",
            description = "Returns paginated adoption requests. Accessible only by admin users. Supports filtering by status, animal id and user id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requests returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Admin access required", content = @Content(schema = @Schema()))
    })
    public PagedModel<AdoptionRequestResponse> getAllRequests(
            @Parameter(description = "Filters requests by status")
            @RequestParam(required = false) AdoptionRequestStatus status,
            @Parameter(description = "Filters requests by animal id")
            @RequestParam(required = false) Long animalId,
            @Parameter(description = "Filters requests by requester user id")
            @RequestParam(required = false) Long userId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return new PagedModel<>(adoptionRequestService.getAllRequests(pageable, status, animalId, userId));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get adoption request by id",
            description = "Returns a specific adoption request. Accessible by requester, animal owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Request or user not found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "User cannot access this adoption request", content = @Content(schema = @Schema()))
    })
    public AdoptionRequestResponse getRequestById(@PathVariable Long id,
                                                  @Parameter(hidden = true)
                                                  Authentication authentication) {
        return adoptionRequestService.getById(id, authentication.getName());
    }

    @PostMapping
    @Operation(
            summary = "Create adoption request",
            description = "Creates a new adoption request for an available animal"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Animal or user not found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Business rule violation while creating request", content = @Content(schema = @Schema()))
    })
    public AdoptionRequestResponse createRequest(@Valid @RequestBody CreateAdoptionRequest request,
                                                 @Parameter(hidden = true)
                                                 Authentication authentication) {
        return adoptionRequestService.save(request, authentication.getName());
    }

    @PatchMapping("/{id}/approve")
    @Operation(
            summary = "Approve adoption request",
            description = "Approves a pending adoption request. Allowed for the animal owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request approved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Request or user not found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Request cannot be approved in the current state", content = @Content(schema = @Schema()))
    })
    public AdoptionRequestResponse approveRequest(@PathVariable Long id,
                                                  @Parameter(hidden = true)
                                                  Authentication authentication) {
        return adoptionRequestService.approveRequest(id, authentication.getName());
    }

    @PatchMapping("/{id}/reject")
    @Operation(
            summary = "Reject adoption request",
            description = "Rejects a pending adoption request. Allowed for the animal owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request rejected successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Request or user not found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Request cannot be rejected in the current state", content = @Content(schema = @Schema()))
    })
    public AdoptionRequestResponse rejectRequest(@PathVariable Long id,
                                                 @Parameter(hidden = true)
                                                 Authentication authentication) {
        return adoptionRequestService.rejectRequest(id, authentication.getName());
    }

    @PatchMapping("/{id}/cancel")
    @Operation(
            summary = "Cancel adoption request",
            description = "Cancels a pending adoption request. Allowed for the requester or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request cancelled successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Request or user not found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Request cannot be cancelled in the current state", content = @Content(schema = @Schema()))
    })
    public AdoptionRequestResponse cancelRequest(@PathVariable Long id,
                                                 @Parameter(hidden = true)
                                                 Authentication authentication) {
        return adoptionRequestService.cancelRequest(id, authentication.getName());
    }
}
