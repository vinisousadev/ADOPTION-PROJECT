package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAnimalRequest;
import br.com.adoption.dto.response.AnimalResponse;
import br.com.adoption.exception.GlobalExceptionHandler;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.service.AnimalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnimalController.class)
@Import(GlobalExceptionHandler.class)
class AnimalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnimalService animalService;

    @Test
    void shouldReturnAvailableAnimals() throws Exception {
        AnimalResponse animal1 = new AnimalResponse();
        animal1.setAnimalName("Rex");
        animal1.setSpecies("Dog");
        animal1.setStatus("AVAILABLE");

        AnimalResponse animal2 = new AnimalResponse();
        animal2.setAnimalName("Mia");
        animal2.setSpecies("Cat");
        animal2.setStatus("AVAILABLE");

        when(animalService.getAvailableAnimals()).thenReturn(List.of(animal1, animal2));

        mockMvc.perform(get("/animals/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].animalName").value("Rex"))
                .andExpect(jsonPath("$[1].animalName").value("Mia"));
    }

    @Test
    void shouldReturnAllAnimals() throws Exception {
        AnimalResponse animal1 = new AnimalResponse();
        animal1.setAnimalName("Rex");
        animal1.setSpecies("Dog");

        AnimalResponse animal2 = new AnimalResponse();
        animal2.setAnimalName("Mia");
        animal2.setSpecies("Cat");

        when(animalService.getAllAnimals()).thenReturn(List.of(animal1, animal2));

        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].animalName").value("Rex"))
                .andExpect(jsonPath("$[1].animalName").value("Mia"));
    }

    @Test
    void shouldCreateAnimal() throws Exception {
        AnimalResponse savedAnimal = new AnimalResponse();
        savedAnimal.setAnimalName("Rex");
        savedAnimal.setSpecies("Dog");
        savedAnimal.setBreed("Labrador");
        savedAnimal.setBirthDate(LocalDate.of(2024, 1, 10));
        savedAnimal.setAge(1);
        savedAnimal.setAnimalSize("MEDIUM");
        savedAnimal.setSex('M');
        savedAnimal.setWeightKg(new BigDecimal("12.50"));
        savedAnimal.setVaccinated('Y');
        savedAnimal.setNeutered('N');
        savedAnimal.setDescription("Very friendly");
        savedAnimal.setStatus("AVAILABLE");
        savedAnimal.setRegistrationDate(LocalDateTime.now());
        savedAnimal.setUserId(1L);

        when(animalService.save(any(CreateAnimalRequest.class))).thenReturn(savedAnimal);

        String requestBody = """
                {
                  "animalName": "Rex",
                  "species": "Dog",
                  "breed": "Labrador",
                  "birthDate": "2024-01-10",
                  "age": 1,
                  "animalSize": "MEDIUM",
                  "sex": "M",
                  "weightKg": 12.50,
                  "vaccinated": "Y",
                  "neutered": "N",
                  "description": "Very friendly",
                  "registrationDate": "2026-04-18T10:30:00",
                  "userId": 1
                }
                """;

        mockMvc.perform(post("/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animalName").value("Rex"))
                .andExpect(jsonPath("$.species").value("Dog"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void shouldReturnNotFoundWhenOwnerDoesNotExist() throws Exception {
        when(animalService.save(any(CreateAnimalRequest.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        String requestBody = """
                {
                  "animalName": "Rex",
                  "species": "Dog",
                  "userId": 99
                }
                """;

        mockMvc.perform(post("/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }
    @Test
    void shouldReturnBadRequestWhenAnimalRequestIsInvalid() throws Exception {
        String longDescription = "x".repeat(501);

        String requestBody = """
            {
              "animalName": "",
              "species": "",
              "age": -1,
              "weightKg": 0,
              "vaccinated": null,
              "neutered": null,
              "description": "%s"
            }
            """.formatted(longDescription);

        mockMvc.perform(post("/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fields.animalName").exists())
                .andExpect(jsonPath("$.fields.species").exists())
                .andExpect(jsonPath("$.fields.age").exists())
                .andExpect(jsonPath("$.fields.weightKg").exists())
                .andExpect(jsonPath("$.fields.vaccinated").exists())
                .andExpect(jsonPath("$.fields.neutered").exists())
                .andExpect(jsonPath("$.fields.registrationDate").exists())
                .andExpect(jsonPath("$.fields.userId").exists());

        verify(animalService, never()).save(any(CreateAnimalRequest.class));
    }
}