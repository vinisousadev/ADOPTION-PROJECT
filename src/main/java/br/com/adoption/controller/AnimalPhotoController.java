package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAnimalPhotoRequest;
import br.com.adoption.dto.response.AnimalPhotoResponse;
import br.com.adoption.service.AnimalPhotoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/animal-photos")
public class AnimalPhotoController {

    private final AnimalPhotoService animalPhotoService;

    public AnimalPhotoController(AnimalPhotoService animalPhotoService) {
        this.animalPhotoService = animalPhotoService;
    }

    @GetMapping
    public List<AnimalPhotoResponse> getAllPhotos() {
        return animalPhotoService.getAllPhotos();
    }

    @PostMapping
    public AnimalPhotoResponse createPhoto(@Valid @RequestBody CreateAnimalPhotoRequest request) {
        return animalPhotoService.save(request);
    }
}