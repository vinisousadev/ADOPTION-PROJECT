package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateAdoptionRequest;
import br.com.adoption.dto.request.UpdateRequestStatusRequest;
import br.com.adoption.dto.response.AdoptionRequestResponse;
import br.com.adoption.entity.AdoptionRequestStatus;
import br.com.adoption.exception.AdoptionRequestNotPendingException;
import br.com.adoption.exception.DuplicateAdoptionRequestException;
import br.com.adoption.exception.GlobalExceptionHandler;
import br.com.adoption.service.AdoptionRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdoptionRequestController.class)
@Import(GlobalExceptionHandler.class)
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

        when(adoptionRequestService.getAllRequests()).thenReturn(List.of(request1, request2));

        mockMvc.perform(get("/adoption-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].message").value("Request 1"))
                .andExpect(jsonPath("$[1].message").value("Request 2"));
    }

    @Test
    void shouldCreateRequestSuccessfully() throws Exception {
        AdoptionRequestResponse response = new AdoptionRequestResponse();
        response.setMessage("I want to adopt Nina");
        response.setStatus(AdoptionRequestStatus.PENDING);
        response.setAnimalId(10L);
        response.setUserId(2L);

        when(adoptionRequestService.save(any(CreateAdoptionRequest.class))).thenReturn(response);

        String requestBody = """
                {
                  "message": "I want to adopt Nina",
                  "animalId": 10,
                  "userId": 2
                }
                """;

        mockMvc.perform(post("/adoption-requests")
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
        when(adoptionRequestService.save(any(CreateAdoptionRequest.class)))
                .thenThrow(new DuplicateAdoptionRequestException("User already has a pending request for this animal"));

        String requestBody = """
                {
                  "message": "I want to adopt Nina",
                  "animalId": 10,
                  "userId": 2
                }
                """;

        mockMvc.perform(post("/adoption-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldApproveRequestSuccessfully() throws Exception {
        AdoptionRequestResponse response = new AdoptionRequestResponse();
        response.setStatus(AdoptionRequestStatus.APPROVED);

        when(adoptionRequestService.approveRequest(eq(20L), any(UpdateRequestStatusRequest.class)))
                .thenReturn(response);

        String requestBody = """
                {
                  "userId": 1
                }
                """;

        mockMvc.perform(patch("/adoption-requests/20/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldReturnConflictWhenApprovingInvalidRequest() throws Exception {
        when(adoptionRequestService.approveRequest(eq(20L), any(UpdateRequestStatusRequest.class)))
                .thenThrow(new AdoptionRequestNotPendingException("Only pending requests can be approved"));

        String requestBody = """
                {
                  "userId": 1
                }
                """;

        mockMvc.perform(patch("/adoption-requests/20/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectRequestSuccessfully() throws Exception {
        AdoptionRequestResponse response = new AdoptionRequestResponse();
        response.setStatus(AdoptionRequestStatus.REJECTED);

        when(adoptionRequestService.rejectRequest(eq(20L), any(UpdateRequestStatusRequest.class)))
                .thenReturn(response);

        String requestBody = """
                {
                  "userId": 1
                }
                """;

        mockMvc.perform(patch("/adoption-requests/20/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void shouldReturnConflictWhenRejectingInvalidRequest() throws Exception {
        when(adoptionRequestService.rejectRequest(eq(20L), any(UpdateRequestStatusRequest.class)))
                .thenThrow(new AdoptionRequestNotPendingException("Only pending requests can be rejected"));

        String requestBody = """
                {
                  "userId": 1
                }
                """;

        mockMvc.perform(patch("/adoption-requests/20/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }
    @Test
    void shouldReturnBadRequestWhenCreateAdoptionRequestIsInvalid() throws Exception {
        String longMessage = "x".repeat(501);

        String requestBody = """
            {
              "message": "%s",
              "animalId": 0,
              "userId": 0
            }
            """.formatted(longMessage);

        mockMvc.perform(post("/adoption-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fields.message").exists())
                .andExpect(jsonPath("$.fields.animalId").exists())
                .andExpect(jsonPath("$.fields.userId").exists());
    }
    @Test
    void shouldReturnBadRequestWhenUpdateRequestStatusIsInvalid() throws Exception {
        String requestBody = """
            {
              "userId": 0
            }
            """;

        mockMvc.perform(patch("/adoption-requests/20/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fields.userId").exists());
    }
}