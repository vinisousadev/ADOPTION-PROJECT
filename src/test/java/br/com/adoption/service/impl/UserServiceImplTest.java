package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateUserRequest;
import br.com.adoption.dto.response.UserResponse;
import br.com.adoption.entity.User;
import br.com.adoption.entity.UserType;
import br.com.adoption.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldReturnAllUsersOrderedById() {
        User user1 = new User();
        user1.setName("Joao");
        user1.setEmail("joao@email.com");
        user1.setUserType(UserType.COMMON);

        User user2 = new User();
        user2.setName("Maria");
        user2.setEmail("maria@email.com");
        user2.setUserType(UserType.COMMON);

        when(userRepository.findAll(Sort.by("id"))).thenReturn(List.of(user1, user2));

        List<UserResponse> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Joao", result.get(0).getName());
        assertEquals("Maria", result.get(1).getName());

        verify(userRepository, times(1)).findAll(Sort.by("id"));
    }

    @Test
    void shouldSaveUserAndEncodePasswordAndForceCommonType() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Carlos");
        request.setCpf("12345678900");
        request.setPhone("83999999999");
        request.setEmail("carlos@email.com");
        request.setCity("Joao Pessoa");
        request.setState("PB");
        request.setRegistrationDate(LocalDateTime.now());
        request.setPasswordHash("123456");

        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.save(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();

        assertNotNull(result);
        assertEquals("Carlos", result.getName());
        assertEquals("carlos@email.com", result.getEmail());
        assertEquals(UserType.COMMON, result.getUserType());

        assertEquals("Carlos", capturedUser.getName());
        assertEquals("12345678900", capturedUser.getCpf());
        assertEquals("encoded-password", capturedUser.getPasswordHash());
        assertEquals(UserType.COMMON, capturedUser.getUserType());

        verify(passwordEncoder, times(1)).encode("123456");
    }
}