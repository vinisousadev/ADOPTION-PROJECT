package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAnimalPhotoRequest;
import br.com.adoption.dto.request.PatchAnimalPhotoRequest;
import br.com.adoption.dto.request.UpdateAnimalPhotoRequest;
import br.com.adoption.dto.response.AnimalPhotoResponse;
import br.com.adoption.exception.GlobalExceptionHandler;
import br.com.adoption.exception.OnlyOwnerCanManageAnimalException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

        when(animalPhotoService.getAllPhotos(isNull(), any())).thenReturn(new PageImpl<>(List.of(photo1, photo2)));

        mockMvc.perform(get("/animal-photos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].photoUrl").value("https://img.com/photo1.jpg"))
                .andExpect(jsonPath("$.content[0].isMain").value("Y"))
                .andExpect(jsonPath("$.content[1].photoUrl").value("https://img.com/photo2.jpg"))
                .andExpect(jsonPath("$.content[1].isMain").value("N"))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void shouldReturnPhotosFilteredByAnimalId() throws Exception {
        AnimalPhotoResponse photo1 = new AnimalPhotoResponse();
        photo1.setPhotoUrl("https://img.com/photo1.jpg");
        photo1.setIsMain('Y');
        photo1.setAnimalId(10L);

        AnimalPhotoResponse photo2 = new AnimalPhotoResponse();
        photo2.setPhotoUrl("https://img.com/photo2.jpg");
        photo2.setIsMain('N');
        photo2.setAnimalId(10L);

        when(animalPhotoService.getAllPhotos(eq(10L), any())).thenReturn(new PageImpl<>(List.of(photo1, photo2)));

        mockMvc.perform(get("/animal-photos").param("animalId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].animalId").value(10))
                .andExpect(jsonPath("$.content[1].animalId").value(10))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void shouldReturnPhotoById() throws Exception {
        AnimalPhotoResponse photo = new AnimalPhotoResponse();
        photo.setId(1L);
        photo.setPhotoUrl("https://img.com/photo1.jpg");
        photo.setIsMain('Y');
        photo.setAnimalId(1L);

        when(animalPhotoService.getById(1L)).thenReturn(photo);

        mockMvc.perform(get("/animal-photos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.photoUrl").value("https://img.com/photo1.jpg"))
                .andExpect(jsonPath("$.isMain").value("Y"))
                .andExpect(jsonPath("$.animalId").value(1));
    }

    @Test
    void shouldCreatePhoto() throws Exception {
        AnimalPhotoResponse savedPhoto = new AnimalPhotoResponse();
        savedPhoto.setPhotoUrl("https://img.com/rex-main.jpg");
        savedPhoto.setIsMain('Y');
        savedPhoto.setAnimalId(1L);

        when(animalPhotoService.save(any(CreateAnimalPhotoRequest.class), any()))
                .thenReturn(savedPhoto);

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
        when(animalPhotoService.save(any(CreateAnimalPhotoRequest.class), any()))
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
    void shouldUpdatePhoto() throws Exception {
        AnimalPhotoResponse updatedPhoto = new AnimalPhotoResponse();
        updatedPhoto.setPhotoUrl("https://img.com/rex-updated.jpg");
        updatedPhoto.setIsMain('N');
        updatedPhoto.setAnimalId(1L);

        when(animalPhotoService.update(eq(1L), any(UpdateAnimalPhotoRequest.class), any()))
                .thenReturn(updatedPhoto);

        String requestBody = """
                {
                  "photoUrl": "https://img.com/rex-updated.jpg",
                  "isMain": "N"
                }
                """;

        mockMvc.perform(put("/animal-photos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoUrl").value("https://img.com/rex-updated.jpg"))
                .andExpect(jsonPath("$.isMain").value("N"))
                .andExpect(jsonPath("$.animalId").value(1));
    }

    @Test
    void shouldPatchPhoto() throws Exception {
        AnimalPhotoResponse patchedPhoto = new AnimalPhotoResponse();
        patchedPhoto.setPhotoUrl("https://img.com/rex-patched.jpg");
        patchedPhoto.setIsMain('Y');
        patchedPhoto.setAnimalId(1L);

        when(animalPhotoService.patch(eq(1L), any(PatchAnimalPhotoRequest.class), any()))
                .thenReturn(patchedPhoto);

        String requestBody = """
                {
                  "photoUrl": "https://img.com/rex-patched.jpg"
                }
                """;

        mockMvc.perform(patch("/animal-photos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoUrl").value("https://img.com/rex-patched.jpg"))
                .andExpect(jsonPath("$.isMain").value("Y"))
                .andExpect(jsonPath("$.animalId").value(1));
    }

    @Test
    void shouldDeletePhotoWhenUserIsOwner() throws Exception {
        AnimalPhotoResponse deletedPhoto = new AnimalPhotoResponse();
        deletedPhoto.setPhotoUrl("https://img.com/rex-main.jpg");
        deletedPhoto.setIsMain('Y');
        deletedPhoto.setAnimalId(1L);

        when(animalPhotoService.delete(eq(1L), any())).thenReturn(deletedPhoto);

        mockMvc.perform(delete("/animal-photos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoUrl").value("https://img.com/rex-main.jpg"))
                .andExpect(jsonPath("$.isMain").value("Y"))
                .andExpect(jsonPath("$.animalId").value(1));
    }

    @Test
    void shouldReturnForbiddenWhenDeletingPhotoFromAnotherOwner() throws Exception {
        when(animalPhotoService.delete(eq(1L), any()))
                .thenThrow(new OnlyOwnerCanManageAnimalException("Only the animal owner or admin can manage this animal"));

        mockMvc.perform(delete("/animal-photos/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Only the animal owner or admin can manage this animal"));
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
