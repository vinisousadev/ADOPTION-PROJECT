package br.com.adoption.service.impl;

import br.com.adoption.dto.request.LoginRequest;
import br.com.adoption.dto.response.LoginResponse;
import br.com.adoption.entity.User;
import br.com.adoption.entity.UserType;
import br.com.adoption.exception.InvalidCredentialsException;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest();
        request.setEmail("carlos@email.com");
        request.setPassword("123456");

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("Carlos");
        when(user.getEmail()).thenReturn("carlos@email.com");
        when(user.getUserType()).thenReturn(UserType.COMMON);
        when(user.getPasswordHash()).thenReturn("encoded-password");

        when(userRepository.findByEmail("carlos@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("fake-jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("Carlos", response.getName());
        assertEquals("carlos@email.com", response.getEmail());
        assertEquals(UserType.COMMON, response.getUserType());
        assertEquals("Login successful", response.getMessage());
        assertEquals("fake-jwt-token", response.getToken());

        verify(userRepository, times(1)).findByEmail("carlos@email.com");
        verify(passwordEncoder, times(1)).matches("123456", "encoded-password");
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {
        LoginRequest request = new LoginRequest();
        request.setEmail("naoexiste@email.com");
        request.setPassword("123456");

        when(userRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("carlos@email.com");
        request.setPassword("senha-errada");

        User user = mock(User.class);
        when(user.getPasswordHash()).thenReturn("encoded-password");

        when(userRepository.findByEmail("carlos@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha-errada", "encoded-password")).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("carlos@email.com");
        verify(passwordEncoder, times(1)).matches("senha-errada", "encoded-password");
        verify(jwtService, never()).generateToken(any(User.class));
    }
}