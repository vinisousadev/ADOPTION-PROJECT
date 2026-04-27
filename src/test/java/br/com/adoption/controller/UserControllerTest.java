package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateUserRequest;
import br.com.adoption.dto.request.PatchUserRequest;
import br.com.adoption.dto.request.UpdateUserRequest;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

        when(userService.getAllUsers(any(), any(), any())).thenReturn(new PageImpl<>(List.of(user1, user2)));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Joao"))
                .andExpect(jsonPath("$.content[1].name").value("Maria"))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void shouldReturnAllUsersWithFilters() throws Exception {
        UserResponse user = new UserResponse();
        user.setName("Joao");
        user.setEmail("joao@email.com");
        user.setUserType(UserType.COMMON);

        when(userService.getAllUsers(any(), any(), any())).thenReturn(new PageImpl<>(List.of(user)));

        mockMvc.perform(get("/users")
                        .param("name", "jo")
                        .param("email", "email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Joao"))
                .andExpect(jsonPath("$.page.totalElements").value(1));

        verify(userService).getAllUsers(any(), eq("jo"), eq("email.com"));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        UserResponse user = new UserResponse();
        user.setId(1L);
        user.setName("Joao");
        user.setEmail("joao@email.com");
        user.setUserType(UserType.COMMON);

        when(userService.getById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Joao"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.userType").value("COMMON"));
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
    void shouldUpdateUser() throws Exception {
        UserResponse updatedUser = new UserResponse();
        updatedUser.setId(1L);
        updatedUser.setName("Carlos atualizado");
        updatedUser.setEmail("carlos@novo.com");

        when(userService.update(eq(1L), any(UpdateUserRequest.class), any()))
                .thenReturn(updatedUser);

        String requestBody = """
                {
                  "name": "Carlos atualizado",
                  "cpf": "12345678900",
                  "phone": "83999999999",
                  "email": "carlos@novo.com",
                  "city": "Joao Pessoa",
                  "state": "PB",
                  "passwordHash": "123456"
                }
                """;

        mockMvc.perform(put("/users/1")
                        .with(user("owner@email.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Carlos atualizado"))
                .andExpect(jsonPath("$.email").value("carlos@novo.com"));
    }

    @Test
    void shouldPatchUser() throws Exception {
        UserResponse patchedUser = new UserResponse();
        patchedUser.setId(1L);
        patchedUser.setName("Carlos patch");

        when(userService.patch(eq(1L), any(PatchUserRequest.class), any()))
                .thenReturn(patchedUser);

        String requestBody = """
                {
                  "name": "Carlos patch"
                }
                """;

        mockMvc.perform(patch("/users/1")
                        .with(user("owner@email.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Carlos patch"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        UserResponse deletedUser = new UserResponse();
        deletedUser.setId(1L);
        deletedUser.setName("Carlos");

        when(userService.delete(eq(1L), any()))
                .thenReturn(deletedUser);

        mockMvc.perform(delete("/users/1")
                        .with(user("owner@email.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Carlos"));
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
