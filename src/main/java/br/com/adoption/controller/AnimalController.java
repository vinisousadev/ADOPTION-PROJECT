package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAnimalRequest;
import br.com.adoption.dto.request.PatchAnimalRequest;
import br.com.adoption.dto.request.UpdateAnimalRequest;
import br.com.adoption.dto.response.AnimalResponse;
import br.com.adoption.service.AnimalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/animals")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping("/available")
    public PagedModel<AnimalResponse> getAvailableAnimals(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return new PagedModel<>(animalService.getAvailableAnimals(pageable));
    }

    @GetMapping("/mine")
    public PagedModel<AnimalResponse> getMyAnimals(Authentication authentication,
                                                   @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return new PagedModel<>(animalService.getMyAnimals(authentication.getName(), pageable));
    }

    @GetMapping("/{id}")
    public AnimalResponse getAnimalById(@PathVariable Long id,
                                        Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return animalService.getById(id, userEmail);
    }

    @PostMapping
    public AnimalResponse createAnimal(@Valid @RequestBody CreateAnimalRequest request,
                                       Authentication authentication) {
        return animalService.save(request, authentication.getName());
    }

    @PutMapping("/{id}")
    public AnimalResponse updateAnimal(@PathVariable Long id,
                                       @Valid @RequestBody UpdateAnimalRequest request,
                                       Authentication authentication) {
        return animalService.update(id, request, authentication.getName());
    }

    @PatchMapping("/{id}")
    public AnimalResponse patchAnimal(@PathVariable Long id,
                                      @Valid @RequestBody PatchAnimalRequest request,
                                      Authentication authentication) {
        return animalService.patch(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    public AnimalResponse deleteAnimal(@PathVariable Long id,
                                       Authentication authentication) {
        return animalService.delete(id, authentication.getName());
    }

    @GetMapping
    public PagedModel<AnimalResponse> getAllAnimals(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return new PagedModel<>(animalService.getAllAnimals(pageable));
    }
}
