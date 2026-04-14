package br.com.adoption.controller;

import br.com.adoption.entity.AnimalPhoto;
import br.com.adoption.service.AnimalPhotoService;
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
    public List<AnimalPhoto> getAllPhotos() {
        return animalPhotoService.getAllPhotos();
    }

    @PostMapping
    public AnimalPhoto createPhoto(@RequestBody AnimalPhoto animalPhoto) {
        return animalPhotoService.save(animalPhoto);
    }
}