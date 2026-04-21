package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateUserRequest;
import br.com.adoption.dto.response.UserResponse;
import br.com.adoption.entity.UserType;
import br.com.adoption.exception.GlobalExceptionHandler;
import br.com.adoption.security.JwtAuthenticationFilter;
import br.com.adoption.service.UserService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldReturnAllUsers() throws Exception {
        UserResponse user1 = new UserResponse();
        user1.setName("Joao");
        user1.setEmail("joao@email.com");
        user1.setUserType(UserType.COMMON);

        UserResponse user2 = new UserResponse();
        user2.setName("Maria");
        user2.setEmail("maria@email.com");
        user2.setUserType(UserType.COMMON);

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Joao"))
                .andExpect(jsonPath("$[1].name").value("Maria"));
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserResponse savedUser = new UserResponse();
        savedUser.setName("Carlos");
        savedUser.setCpf("12345678900");
        savedUser.setPhone("83999999999");
        savedUser.setEmail("carlos@email.com");
        savedUser.setCity("Joao Pessoa");
        savedUser.setState("PB");
        savedUser.setRegistrationDate(LocalDateTime.now());
        savedUser.setUserType(UserType.COMMON);

        when(userService.save(any(CreateUserRequest.class))).thenReturn(savedUser);

        String requestBody = """
                {
                  "name": "Carlos",
                  "cpf": "12345678900",
                  "phone": "83999999999",
                  "email": "carlos@email.com",
                  "city": "Joao Pessoa",
                  "state": "PB",
                  "passwordHash": "123456"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos"))
                .andExpect(jsonPath("$.email").value("carlos@email.com"))
                .andExpect(jsonPath("$.userType").value("COMMON"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                  "name": "",
                  "cpf": "",
                  "email": "email-invalido",
                  "passwordHash": ""
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fields.name").exists())
                .andExpect(jsonPath("$.fields.cpf").exists())
                .andExpect(jsonPath("$.fields.email").exists())
                .andExpect(jsonPath("$.fields.passwordHash").exists());

        verify(userService, never()).save(any(CreateUserRequest.class));
    }
}
