package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAdoptionRequest;
import br.com.adoption.dto.response.AdoptionRequestResponse;
import br.com.adoption.entity.AdoptionRequestStatus;
import br.com.adoption.exception.AdoptionRequestNotPendingException;
import br.com.adoption.exception.DuplicateAdoptionRequestException;
import br.com.adoption.exception.GlobalExceptionHandler;
import br.com.adoption.security.JwtAuthenticationFilter;
import br.com.adoption.service.AdoptionRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AdoptionRequestController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@Import(GlobalExceptionHandler.class)
@WithMockUser
class AdoptionRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdoptionRequestService adoptionRequestService;



    @Test
    void shouldReturnAllRequests() throws Exception {
        AdoptionRequestResponse request1 = new AdoptionRequestResponse();
        request1.setMessage("Request 1");
        request1.setStatus(AdoptionRequestStatus.PENDING);

        AdoptionRequestResponse request2 = new AdoptionRequestResponse();
        request2.setMessage("Request 2");
        request2.setStatus(AdoptionRequestStatus.APPROVED);

        when(adoptionRequestService.getAllRequests(any())).thenReturn(new PageImpl<>(List.of(request1, request2)));

        mockMvc.perform(get("/adoption-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].message").value("Request 1"))
                .andExpect(jsonPath("$.content[1].message").value("Request 2"))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void shouldReturnRequestById() throws Exception {
        AdoptionRequestResponse response = new AdoptionRequestResponse();
        response.setId(20L);
        response.setMessage("Request by id");
        response.setStatus(AdoptionRequestStatus.PENDING);

        when(adoptionRequestService.getById(eq(20L), eq("user"))).thenReturn(response);

        mockMvc.perform(get("/adoption-requests/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.message").value("Request by id"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldCreateRequestSuccessfully() throws Exception {
        AdoptionRequestResponse response = new AdoptionRequestResponse();
        response.setMessage("I want to adopt Nina");
        response.setStatus(AdoptionRequestStatus.PENDING);
        response.setAnimalId(10L);
        response.setUserId(2L);

        when(adoptionRequestService.save(any(CreateAdoptionRequest.class), eq("user"))).thenReturn(response);

        String requestBody = """
                {
                  "message": "I want to adopt Nina",
                  "animalId": 10
                }
                """;

        mockMvc.perform(post("/adoption-requests")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("I want to adopt Nina"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.animalId").value(10))
                .andExpect(jsonPath("$.userId").value(2));
    }

    @Test
    void shouldReturnConflictWhenCreatingDuplicateRequest() throws Exception {
        when(adoptionRequestService.save(any(CreateAdoptionRequest.class), eq("user")))
                .thenThrow(new DuplicateAdoptionRequestException("User already has a pending request for this animal"));

        String requestBody = """
                {
                  "message": "I want to adopt Nina",
                  "animalId": 10
                }
                """;

        mockMvc.perform(post("/adoption-requests")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldApproveRequestSuccessfully() throws Exception {
        AdoptionRequestResponse response = new AdoptionRequestResponse();
        response.setStatus(AdoptionRequestStatus.APPROVED);

        when(adoptionRequestService.approveRequest(eq(20L), eq("owner@email.com")))
                .thenReturn(response);

        mockMvc.perform(patch("/adoption-requests/20/approve")
                        .with(csrf())
                        .with(user("owner@email.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldReturnConflictWhenApprovingInvalidRequest() throws Exception {
        when(adoptionRequestService.approveRequest(eq(20L), eq("owner@email.com")))
                .thenThrow(new AdoptionRequestNotPendingException("Only pending requests can be approved"));

        mockMvc.perform(patch("/adoption-requests/20/approve")
                        .with(csrf())
                        .with(user("owner@email.com")))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectRequestSuccessfully() throws Exception {
        AdoptionRequestResponse response = new AdoptionRequestResponse();
        response.setStatus(AdoptionRequestStatus.REJECTED);

        when(adoptionRequestService.rejectRequest(eq(20L), eq("owner@email.com")))
                .thenReturn(response);

        mockMvc.perform(patch("/adoption-requests/20/reject")
                        .with(csrf())
                        .with(user("owner@email.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void shouldReturnConflictWhenRejectingInvalidRequest() throws Exception {
        when(adoptionRequestService.rejectRequest(eq(20L), eq("owner@email.com")))
                .thenThrow(new AdoptionRequestNotPendingException("Only pending requests can be rejected"));

        mockMvc.perform(patch("/adoption-requests/20/reject")
                        .with(csrf())
                .with(user("owner@email.com")))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldCancelRequestSuccessfully() throws Exception {
        AdoptionRequestResponse response = new AdoptionRequestResponse();
        response.setStatus(AdoptionRequestStatus.CANCELLED);

        when(adoptionRequestService.cancelRequest(eq(20L), eq("requester@email.com")))
                .thenReturn(response);

        mockMvc.perform(patch("/adoption-requests/20/cancel")
                        .with(csrf())
                        .with(user("requester@email.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void shouldReturnConflictWhenCancellingInvalidRequest() throws Exception {
        when(adoptionRequestService.cancelRequest(eq(20L), eq("requester@email.com")))
                .thenThrow(new AdoptionRequestNotPendingException("Only pending requests can be canceled"));

        mockMvc.perform(patch("/adoption-requests/20/cancel")
                        .with(csrf())
                        .with(user("requester@email.com")))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestWhenCreateAdoptionRequestIsInvalid() throws Exception {
        String longMessage = "x".repeat(501);

        String requestBody = """
                {
                  "message": "%s",
                  "animalId": 0
                }
                """.formatted(longMessage);

        mockMvc.perform(post("/adoption-requests")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fields.message").exists())
                .andExpect(jsonPath("$.fields.animalId").exists());
    }
}
