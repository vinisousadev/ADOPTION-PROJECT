package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAnimalRequest;
import br.com.adoption.dto.response.AnimalResponse;
import br.com.adoption.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/animals")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping("/available")
    public List<AnimalResponse> getAvailableAnimals() {
        return animalService.getAvailableAnimals();
    }

    @PostMapping
    public AnimalResponse createAnimal(@Valid @RequestBody CreateAnimalRequest request) {
        return animalService.save(request);
    }

    @GetMapping
    public List<AnimalResponse> getAllAnimals() {
        return animalService.getAllAnimals();
    }
}