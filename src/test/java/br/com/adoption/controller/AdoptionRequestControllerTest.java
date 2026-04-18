package br.com.adoption.controller;

import br.com.adoption.entity.AdoptionRequest;
import br.com.adoption.entity.AdoptionRequestStatus;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.User;
import br.com.adoption.exception.GlobalExceptionHandler;
import br.com.adoption.exception.OnlyOwnerCanManageAdoptionRequestException;
import br.com.adoption.exception.OwnerCannotAdoptOwnAnimalException;
import br.com.adoption.service.AdoptionRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdoptionRequestControllerTest {

    @Mock
    private AdoptionRequestService adoptionRequestService;

    @InjectMocks
    private AdoptionRequestController adoptionRequestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(adoptionRequestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldReturnAllAdoptionRequests() throws Exception {
        AdoptionRequest request1 = new AdoptionRequest();
        request1.setMessage("First request");
        request1.setStatus(AdoptionRequestStatus.PENDING);

        AdoptionRequest request2 = new AdoptionRequest();
        request2.setMessage("Second request");
        request2.setStatus(AdoptionRequestStatus.APPROVED);

        when(adoptionRequestService.getAllRequests()).thenReturn(List.of(request1, request2));

        mockMvc.perform(get("/adoption-requests"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].message").value("First request"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].message").value("Second request"))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));

        verify(adoptionRequestService, times(1)).getAllRequests();
    }

    @Test
    void shouldCreateAdoptionRequest() throws Exception {
        User adopter = new User();
        setId(adopter, 2L);
        adopter.setName("Ana Adopter");

        Animal animal = new Animal();
        setId(animal, 10L);

        AdoptionRequest requestBody = new AdoptionRequest();
        requestBody.setMessage("I want to adopt Bob");
        requestBody.setAnimal(animal);
        requestBody.setUser(adopter);

        AdoptionRequest savedRequest = new AdoptionRequest();
        setId(savedRequest, 100L);
        savedRequest.setMessage("I want to adopt Bob");
        savedRequest.setStatus(AdoptionRequestStatus.PENDING);
        savedRequest.setRequestDate(LocalDateTime.now());
        savedRequest.setResponseDate(null);
        savedRequest.setAnimal(animal);
        savedRequest.setUser(adopter);

        when(adoptionRequestService.save(any(AdoptionRequest.class))).thenReturn(savedRequest);

        mockMvc.perform(post("/adoption-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("I want to adopt Bob"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(adoptionRequestService, times(1)).save(any(AdoptionRequest.class));
    }

    @Test
    void shouldReturnConflictWhenOwnerTriesToCreateOwnAdoptionRequest() throws Exception {
        User owner = new User();
        setId(owner, 1L);

        Animal animal = new Animal();
        setId(animal, 10L);

        AdoptionRequest requestBody = new AdoptionRequest();
        requestBody.setMessage("I want to adopt my own animal");
        requestBody.setAnimal(animal);
        requestBody.setUser(owner);

        when(adoptionRequestService.save(any(AdoptionRequest.class)))
                .thenThrow(new OwnerCannotAdoptOwnAnimalException(
                        "The owner cannot create an adoption request for their own animal"
                ));

        mockMvc.perform(post("/adoption-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("The owner cannot create an adoption request for their own animal"));

        verify(adoptionRequestService, times(1)).save(any(AdoptionRequest.class));
    }

    @Test
    void shouldApproveAdoptionRequest() throws Exception {
        AdoptionRequest approvedRequest = new AdoptionRequest();
        setId(approvedRequest, 100L);
        approvedRequest.setMessage("I want to adopt Bob");
        approvedRequest.setStatus(AdoptionRequestStatus.APPROVED);
        approvedRequest.setRequestDate(LocalDateTime.now().minusDays(1));
        approvedRequest.setResponseDate(LocalDateTime.now());

        when(adoptionRequestService.approveRequest(100L, 1L)).thenReturn(approvedRequest);

        mockMvc.perform(patch("/adoption-requests/100/approve")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.message").value("I want to adopt Bob"));

        verify(adoptionRequestService, times(1)).approveRequest(100L, 1L);
    }

    @Test
    void shouldReturnConflictWhenNonOwnerTriesToApproveRequest() throws Exception {
        when(adoptionRequestService.approveRequest(100L, 3L))
                .thenThrow(new OnlyOwnerCanManageAdoptionRequestException(
                        "Only the animal owner can approve this request"
                ));

        mockMvc.perform(patch("/adoption-requests/100/approve")
                        .param("userId", "3"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Only the animal owner can approve this request"));

        verify(adoptionRequestService, times(1)).approveRequest(100L, 3L);
    }

    @Test
    void shouldRejectAdoptionRequest() throws Exception {
        AdoptionRequest rejectedRequest = new AdoptionRequest();
        setId(rejectedRequest, 100L);
        rejectedRequest.setMessage("I want to adopt Bob");
        rejectedRequest.setStatus(AdoptionRequestStatus.REJECTED);
        rejectedRequest.setRequestDate(LocalDateTime.now().minusDays(1));
        rejectedRequest.setResponseDate(LocalDateTime.now());

        when(adoptionRequestService.rejectRequest(100L, 1L)).thenReturn(rejectedRequest);

        mockMvc.perform(patch("/adoption-requests/100/reject")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.message").value("I want to adopt Bob"));

        verify(adoptionRequestService, times(1)).rejectRequest(100L, 1L);
    }

    @Test
    void shouldReturnConflictWhenNonOwnerTriesToRejectRequest() throws Exception {
        when(adoptionRequestService.rejectRequest(100L, 3L))
                .thenThrow(new OnlyOwnerCanManageAdoptionRequestException(
                        "Only the animal owner can reject this request"
                ));

        mockMvc.perform(patch("/adoption-requests/100/reject")
                        .param("userId", "3"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Only the animal owner can reject this request"));

        verify(adoptionRequestService, times(1)).rejectRequest(100L, 3L);
    }

    private void setId(Object obj, Long idValue) throws Exception {
        Field field = obj.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(obj, idValue);
    }
}