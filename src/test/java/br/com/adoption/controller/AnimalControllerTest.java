package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAnimalRequest;
import br.com.adoption.dto.request.UpdateAnimalRequest;
import br.com.adoption.dto.response.AnimalResponse;
import br.com.adoption.exception.GlobalExceptionHandler;
import br.com.adoption.exception.OnlyOwnerCanManageAnimalException;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.security.JwtAuthenticationFilter;
import br.com.adoption.service.AnimalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AnimalController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@Import(GlobalExceptionHandler.class)
@WithMockUser
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
    void shouldReturnAnimalById() throws Exception {
        AnimalResponse animal = new AnimalResponse();
        animal.setAnimalName("Rex");
        animal.setSpecies("Dog");
        animal.setStatus("AVAILABLE");
        animal.setUserId(1L);

        when(animalService.getById(eq(10L), anyString())).thenReturn(animal);

        mockMvc.perform(get("/animals/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animalName").value("Rex"))
                .andExpect(jsonPath("$.species").value("Dog"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.userId").value(1));
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

        when(animalService.save(any(CreateAnimalRequest.class), anyString())).thenReturn(savedAnimal);

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
                  "registrationDate": "2026-04-18T10:30:00"
                }
                """;

        mockMvc.perform(post("/animals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animalName").value("Rex"))
                .andExpect(jsonPath("$.species").value("Dog"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void shouldUpdateAnimal() throws Exception {
        AnimalResponse updatedAnimal = new AnimalResponse();
        updatedAnimal.setAnimalName("Rex atualizado");
        updatedAnimal.setSpecies("Dog");
        updatedAnimal.setStatus("AVAILABLE");
        updatedAnimal.setUserId(1L);

        when(animalService.update(eq(10L), any(UpdateAnimalRequest.class), anyString()))
                .thenReturn(updatedAnimal);

        String requestBody = """
                {
                  "animalName": "Rex atualizado",
                  "species": "Dog",
                  "breed": "Labrador",
                  "birthDate": "2024-01-10",
                  "age": 2,
                  "animalSize": "MEDIUM",
                  "sex": "M",
                  "weightKg": 14.50,
                  "vaccinated": "Y",
                  "neutered": "Y",
                  "description": "Very friendly"
                }
                """;

        mockMvc.perform(put("/animals/10")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animalName").value("Rex atualizado"))
                .andExpect(jsonPath("$.species").value("Dog"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void shouldReturnForbiddenWhenUpdatingAnimalFromAnotherOwner() throws Exception {
        when(animalService.update(eq(10L), any(UpdateAnimalRequest.class), anyString()))
                .thenThrow(new OnlyOwnerCanManageAnimalException("Only the animal owner or admin can manage this animal"));

        String requestBody = """
                {
                  "animalName": "Rex atualizado",
                  "species": "Dog",
                  "vaccinated": "Y",
                  "neutered": "Y"
                }
                """;

        mockMvc.perform(put("/animals/10")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Only the animal owner or admin can manage this animal"));
    }

    @Test
    void shouldSoftDeleteAnimal() throws Exception {
        AnimalResponse deletedAnimal = new AnimalResponse();
        deletedAnimal.setAnimalName("Rex");
        deletedAnimal.setSpecies("Dog");
        deletedAnimal.setStatus("REMOVED");
        deletedAnimal.setUserId(1L);

        when(animalService.delete(eq(10L), anyString())).thenReturn(deletedAnimal);

        mockMvc.perform(delete("/animals/10")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animalName").value("Rex"))
                .andExpect(jsonPath("$.species").value("Dog"))
                .andExpect(jsonPath("$.status").value("REMOVED"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void shouldReturnForbiddenWhenDeletingAnimalFromAnotherOwner() throws Exception {
        when(animalService.delete(eq(10L), anyString()))
                .thenThrow(new OnlyOwnerCanManageAnimalException("Only the animal owner or admin can manage this animal"));

        mockMvc.perform(delete("/animals/10")
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Only the animal owner or admin can manage this animal"));
    }

    @Test
    void shouldReturnNotFoundWhenOwnerDoesNotExist() throws Exception {
        when(animalService.save(any(CreateAnimalRequest.class), anyString()))
                .thenThrow(new ResourceNotFoundException("User not found"));

        String requestBody = """
                {
                  "animalName": "Rex",
                  "species": "Dog",
                  "vaccinated": "Y",
                  "neutered": "N",
                  "registrationDate": "2026-04-18T10:30:00"
                }
                """;

        mockMvc.perform(post("/animals")
                        .with(csrf())
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
                        .with(csrf())
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
                .andExpect(jsonPath("$.fields.registrationDate").exists());

        verify(animalService, never()).save(any(CreateAnimalRequest.class), anyString());
    }
}
