package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAnimalPhotoRequest;
import br.com.adoption.dto.request.PatchAnimalPhotoRequest;
import br.com.adoption.dto.request.UpdateAnimalPhotoRequest;
import br.com.adoption.dto.response.AnimalPhotoResponse;
import br.com.adoption.service.AnimalPhotoService;
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
@Tag(name = "Animal Photos", description = "Animal photo endpoints")
@RestController
@RequestMapping("/animal-photos")
public class AnimalPhotoController {

    private final AnimalPhotoService animalPhotoService;

    public AnimalPhotoController(AnimalPhotoService animalPhotoService) {
        this.animalPhotoService = animalPhotoService;
    }

    @GetMapping
    @Operation(
            summary = "List animal photos",
            description = "Returns paginated photos. Supports optional filtering by animal id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photos returned successfully")
    })
    public PagedModel<AnimalPhotoResponse> getAllPhotos(
            @Parameter(description = "Filters photos by animal id")
            @RequestParam(required = false) Long animalId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return new PagedModel<>(animalPhotoService.getAllPhotos(animalId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get animal photo by id",
            description = "Returns a specific animal photo"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo returned successfully"),
            @ApiResponse(responseCode = "404", description = "Photo not found", content = @Content(schema = @Schema()))
    })
    public AnimalPhotoResponse getPhotoById(@PathVariable Long id) {
        return animalPhotoService.getById(id);
    }

    @PostMapping
    @Operation(
            summary = "Create animal photo",
            description = "Creates a new animal photo. Allowed for the animal owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can manage animal photos", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Animal or user not found", content = @Content(schema = @Schema()))
    })
    public AnimalPhotoResponse createPhoto(@Valid @RequestBody CreateAnimalPhotoRequest request,
                                           @Parameter(hidden = true)
                                           Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return animalPhotoService.save(request, userEmail);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Replace animal photo",
            description = "Fully updates an animal photo. Allowed for the animal owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can manage animal photos", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Photo or user not found", content = @Content(schema = @Schema()))
    })
    public AnimalPhotoResponse updatePhoto(@PathVariable Long id,
                                           @Valid @RequestBody UpdateAnimalPhotoRequest request,
                                           @Parameter(hidden = true)
                                           Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return animalPhotoService.update(id, request, userEmail);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Partially update animal photo",
            description = "Partially updates an animal photo. Allowed for the animal owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can manage animal photos", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Photo or user not found", content = @Content(schema = @Schema()))
    })
    public AnimalPhotoResponse patchPhoto(@PathVariable Long id,
                                          @Valid @RequestBody PatchAnimalPhotoRequest request,
                                          @Parameter(hidden = true)
                                          Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return animalPhotoService.patch(id, request, userEmail);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete animal photo",
            description = "Deletes an animal photo. Allowed for the animal owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can manage animal photos", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Photo or user not found", content = @Content(schema = @Schema()))
    })
    public AnimalPhotoResponse deletePhoto(@PathVariable Long id,
                                           @Parameter(hidden = true)
                                           Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return animalPhotoService.delete(id, userEmail);
    }
}
