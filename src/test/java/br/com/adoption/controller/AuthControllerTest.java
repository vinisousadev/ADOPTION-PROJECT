package br.com.adoption.controller;

import br.com.adoption.dto.request.LoginRequest;
import br.com.adoption.dto.response.LoginResponse;
import br.com.adoption.entity.UserType;
import br.com.adoption.exception.GlobalExceptionHandler;
import br.com.adoption.exception.InvalidCredentialsException;
import br.com.adoption.security.JwtAuthenticationFilter;
import br.com.adoption.service.AuthService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginResponse response = new LoginResponse();
        response.setUserId(1L);
        response.setName("Carlos");
        response.setEmail("carlos@email.com");
        response.setUserType(UserType.COMMON);
        response.setMessage("Login successful");
        response.setToken("fake-jwt-token");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        String requestBody = """
                {
                  "email": "carlos@email.com",
                  "password": "123456"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("Carlos"))
                .andExpect(jsonPath("$.email").value("carlos@email.com"))
                .andExpect(jsonPath("$.userType").value("COMMON"))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        String requestBody = """
                {
                  "email": "carlos@email.com",
                  "password": "senha-errada"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void shouldReturnBadRequestWhenLoginRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                  "email": "email-invalido",
                  "password": ""
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fields.email").exists())
                .andExpect(jsonPath("$.fields.password").exists());
    }
}
