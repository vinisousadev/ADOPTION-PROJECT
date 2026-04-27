package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAnimalRequest;
import br.com.adoption.dto.request.PatchAnimalRequest;
import br.com.adoption.dto.request.UpdateAnimalRequest;
import br.com.adoption.dto.response.AnimalResponse;
import br.com.adoption.entity.AnimalStatus;
import br.com.adoption.service.AnimalService;
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
@Tag(name = "Animals", description = "Animal management and search endpoints")
@RestController
@RequestMapping("/animals")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping("/available")
    @Operation(
            summary = "List available animals",
            description = "Returns paginated available animals. Supports filtering by species, city, size and sex"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Animals returned successfully")
    })
    public PagedModel<AnimalResponse> getAvailableAnimals(
            @Parameter(description = "Filters animals by species")
            @RequestParam(required = false) String species,
            @Parameter(description = "Filters animals by owner city")
            @RequestParam(required = false) String city,
            @Parameter(description = "Filters animals by size")
            @RequestParam(required = false) String animalSize,
            @Parameter(description = "Filters animals by sex. Accepted values: M or F")
            @RequestParam(required = false) Character sex,
            @ParameterObject
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return new PagedModel<>(animalService.getAvailableAnimals(pageable, species, city, animalSize, sex));
    }

    @GetMapping("/mine")
    @Operation(
            summary = "List authenticated user animals",
            description = "Returns paginated animals owned by the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Animals returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema()))
    })
    public PagedModel<AnimalResponse> getMyAnimals(Authentication authentication,
                                                   @ParameterObject
                                                   @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return new PagedModel<>(animalService.getMyAnimals(authentication.getName(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get animal by id",
            description = "Returns a specific animal. Unavailable animals are visible only to the owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Animal returned successfully"),
            @ApiResponse(responseCode = "404", description = "Animal or user not found", content = @Content(schema = @Schema()))
    })
    public AnimalResponse getAnimalById(@PathVariable Long id,
                                        @Parameter(hidden = true)
                                        Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return animalService.getById(id, userEmail);
    }

    @PostMapping
    @Operation(
            summary = "Create animal",
            description = "Creates a new animal linked to the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Animal created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema()))
    })
    public AnimalResponse createAnimal(@Valid @RequestBody CreateAnimalRequest request,
                                       @Parameter(hidden = true)
                                       Authentication authentication) {
        return animalService.save(request, authentication.getName());
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Replace animal",
            description = "Fully updates an animal. Allowed for the animal owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Animal updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can manage this animal", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Animal or user not found", content = @Content(schema = @Schema()))
    })
    public AnimalResponse updateAnimal(@PathVariable Long id,
                                       @Valid @RequestBody UpdateAnimalRequest request,
                                       @Parameter(hidden = true)
                                       Authentication authentication) {
        return animalService.update(id, request, authentication.getName());
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Partially update animal",
            description = "Partially updates an animal. Allowed for the animal owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Animal updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can manage this animal", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Animal or user not found", content = @Content(schema = @Schema()))
    })
    public AnimalResponse patchAnimal(@PathVariable Long id,
                                      @Valid @RequestBody PatchAnimalRequest request,
                                      @Parameter(hidden = true)
                                      Authentication authentication) {
        return animalService.patch(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Remove animal",
            description = "Soft deletes an animal by setting its status to REMOVED. Allowed for the animal owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Animal removed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can manage this animal", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Animal or user not found", content = @Content(schema = @Schema()))
    })
    public AnimalResponse deleteAnimal(@PathVariable Long id,
                                       @Parameter(hidden = true)
                                       Authentication authentication) {
        return animalService.delete(id, authentication.getName());
    }

    @GetMapping
    @Operation(
            summary = "List all animals",
            description = "Returns paginated animals for admin users. Supports filtering by status, species, city, size and sex"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Animals returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Admin access required", content = @Content(schema = @Schema()))
    })
    public PagedModel<AnimalResponse> getAllAnimals(
            @Parameter(description = "Filters animals by status")
            @RequestParam(required = false) AnimalStatus status,
            @Parameter(description = "Filters animals by species")
            @RequestParam(required = false) String species,
            @Parameter(description = "Filters animals by owner city")
            @RequestParam(required = false) String city,
            @Parameter(description = "Filters animals by size")
            @RequestParam(required = false) String animalSize,
            @Parameter(description = "Filters animals by sex. Accepted values: M or F")
            @RequestParam(required = false) Character sex,
            @ParameterObject
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return new PagedModel<>(animalService.getAllAnimals(pageable, status, species, city, animalSize, sex));
    }
}
