package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAnimalPhotoRequest;
import br.com.adoption.dto.response.AnimalPhotoResponse;
import br.com.adoption.exception.GlobalExceptionHandler;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.security.JwtAuthenticationFilter;
import br.com.adoption.service.AnimalPhotoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AnimalPhotoController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AnimalPhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnimalPhotoService animalPhotoService;

    @Test
    void shouldReturnAllPhotos() throws Exception {
        AnimalPhotoResponse photo1 = new AnimalPhotoResponse();
        photo1.setPhotoUrl("https://img.com/photo1.jpg");
        photo1.setIsMain('Y');
        photo1.setAnimalId(1L);

        AnimalPhotoResponse photo2 = new AnimalPhotoResponse();
        photo2.setPhotoUrl("https://img.com/photo2.jpg");
        photo2.setIsMain('N');
        photo2.setAnimalId(1L);

        when(animalPhotoService.getAllPhotos()).thenReturn(List.of(photo1, photo2));

        mockMvc.perform(get("/animal-photos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].photoUrl").value("https://img.com/photo1.jpg"))
                .andExpect(jsonPath("$[0].isMain").value("Y"))
                .andExpect(jsonPath("$[1].photoUrl").value("https://img.com/photo2.jpg"))
                .andExpect(jsonPath("$[1].isMain").value("N"));
    }

    @Test
    void shouldCreatePhoto() throws Exception {
        AnimalPhotoResponse savedPhoto = new AnimalPhotoResponse();
        savedPhoto.setPhotoUrl("https://img.com/rex-main.jpg");
        savedPhoto.setIsMain('Y');
        savedPhoto.setAnimalId(1L);

        when(animalPhotoService.save(any(CreateAnimalPhotoRequest.class))).thenReturn(savedPhoto);

        String requestBody = """
                {
                  "photoUrl": "https://img.com/rex-main.jpg",
                  "isMain": "Y",
                  "animalId": 1
                }
                """;

        mockMvc.perform(post("/animal-photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoUrl").value("https://img.com/rex-main.jpg"))
                .andExpect(jsonPath("$.isMain").value("Y"))
                .andExpect(jsonPath("$.animalId").value(1));
    }

    @Test
    void shouldReturnNotFoundWhenAnimalDoesNotExist() throws Exception {
        when(animalPhotoService.save(any(CreateAnimalPhotoRequest.class)))
                .thenThrow(new ResourceNotFoundException("Animal not found"));

        String requestBody = """
                {
                  "photoUrl": "https://img.com/rex-main.jpg",
                  "isMain": "Y",
                  "animalId": 99
                }
                """;

        mockMvc.perform(post("/animal-photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }
    @Test
    void shouldReturnBadRequestWhenAnimalPhotoRequestIsInvalid() throws Exception {
        String requestBody = """
            {
              "photoUrl": "",
              "isMain": null,
              "animalId": 0
            }
            """;

        mockMvc.perform(post("/animal-photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fields.photoUrl").exists())
                .andExpect(jsonPath("$.fields.isMain").exists())
                .andExpect(jsonPath("$.fields.animalId").exists());
    }
}
