package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAnimalPhotoRequest;
import br.com.adoption.dto.request.PatchAnimalPhotoRequest;
import br.com.adoption.dto.request.UpdateAnimalPhotoRequest;
import br.com.adoption.dto.response.AnimalPhotoResponse;
import br.com.adoption.service.AnimalPhotoService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/animal-photos")
public class AnimalPhotoController {

    private final AnimalPhotoService animalPhotoService;

    public AnimalPhotoController(AnimalPhotoService animalPhotoService) {
        this.animalPhotoService = animalPhotoService;
    }

    @GetMapping
    public PagedModel<AnimalPhotoResponse> getAllPhotos(
            @RequestParam(required = false) Long animalId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return new PagedModel<>(animalPhotoService.getAllPhotos(animalId, pageable));
    }

    @GetMapping("/{id}")
    public AnimalPhotoResponse getPhotoById(@PathVariable Long id) {
        return animalPhotoService.getById(id);
    }

    @PostMapping
    public AnimalPhotoResponse createPhoto(@Valid @RequestBody CreateAnimalPhotoRequest request,
                                           Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return animalPhotoService.save(request, userEmail);
    }

    @PutMapping("/{id}")
    public AnimalPhotoResponse updatePhoto(@PathVariable Long id,
                                           @Valid @RequestBody UpdateAnimalPhotoRequest request,
                                           Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return animalPhotoService.update(id, request, userEmail);
    }

    @PatchMapping("/{id}")
    public AnimalPhotoResponse patchPhoto(@PathVariable Long id,
                                          @Valid @RequestBody PatchAnimalPhotoRequest request,
                                          Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return animalPhotoService.patch(id, request, userEmail);
    }

    @DeleteMapping("/{id}")
    public AnimalPhotoResponse deletePhoto(@PathVariable Long id,
                                           Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return animalPhotoService.delete(id, userEmail);
    }
}
