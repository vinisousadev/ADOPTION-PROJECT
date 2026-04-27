package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateUserRequest;
import br.com.adoption.dto.request.PatchUserRequest;
import br.com.adoption.dto.request.UpdateUserRequest;
import br.com.adoption.dto.response.UserResponse;
import br.com.adoption.entity.User;
import br.com.adoption.entity.UserType;
import br.com.adoption.exception.OnlyOwnerCanManageUserException;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    void shouldReturnFilteredUsersPage() {
        User user = new User();
        user.setName("Joao");
        user.setEmail("joao@email.com");
        user.setUserType(UserType.COMMON);

        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id"));

        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(user), pageable, 1));

        Page<UserResponse> result = userService.getAllUsers(pageable, "jo", "email.com");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Joao", result.getContent().getFirst().getName());
        verify(userRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldReturnUserById() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("Joao");
        when(user.getEmail()).thenReturn("joao@email.com");
        when(user.getUserType()).thenReturn(UserType.COMMON);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Joao", result.getName());
        assertEquals("joao@email.com", result.getEmail());
        assertEquals(UserType.COMMON, result.getUserType());
    }

    @Test
    void shouldThrowExceptionWhenUserByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getById(99L)
        );

        assertEquals("User not found", exception.getMessage());
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
        assertNotNull(capturedUser.getRegistrationDate());

        verify(passwordEncoder, times(1)).encode("123456");
    }

    @Test
    void shouldUpdateUserWhenAuthenticatedUserIsOwner() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Carlos atualizado");
        request.setCpf("12345678900");
        request.setPhone("83999999999");
        request.setEmail("carlos@novo.com");
        request.setCity("Joao Pessoa");
        request.setState("PB");
        request.setPasswordHash("nova-senha");

        User targetUser = mock(User.class);
        when(targetUser.getId()).thenReturn(1L);

        User authenticatedUser = mock(User.class);
        when(authenticatedUser.getId()).thenReturn(1L);
        when(authenticatedUser.getUserType()).thenReturn(UserType.COMMON);

        when(userRepository.findById(1L)).thenReturn(Optional.of(targetUser));
        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(authenticatedUser));
        when(passwordEncoder.encode("nova-senha")).thenReturn("encoded-nova-senha");
        when(userRepository.save(targetUser)).thenReturn(targetUser);

        userService.update(1L, request, "owner@email.com");

        verify(targetUser).setName("Carlos atualizado");
        verify(targetUser).setCpf("12345678900");
        verify(targetUser).setPhone("83999999999");
        verify(targetUser).setEmail("carlos@novo.com");
        verify(targetUser).setCity("Joao Pessoa");
        verify(targetUser).setState("PB");
        verify(targetUser).setPasswordHash("encoded-nova-senha");
        verify(userRepository).save(targetUser);
    }

    @Test
    void shouldPatchUserWhenAuthenticatedUserIsAdmin() {
        PatchUserRequest request = new PatchUserRequest();
        request.setCity("Recife");

        User targetUser = mock(User.class);
        when(targetUser.getId()).thenReturn(1L);

        User adminUser = mock(User.class);
        when(adminUser.getId()).thenReturn(99L);
        when(adminUser.getUserType()).thenReturn(UserType.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(targetUser));
        when(userRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(adminUser));
        when(userRepository.save(targetUser)).thenReturn(targetUser);

        userService.patch(1L, request, "admin@email.com");

        verify(targetUser).setCity("Recife");
        verify(userRepository).save(targetUser);
    }

    @Test
    void shouldDeleteUserWhenAuthenticatedUserIsOwner() {
        User targetUser = mock(User.class);
        when(targetUser.getId()).thenReturn(1L);

        User authenticatedUser = mock(User.class);
        when(authenticatedUser.getId()).thenReturn(1L);
        when(authenticatedUser.getUserType()).thenReturn(UserType.COMMON);

        when(userRepository.findById(1L)).thenReturn(Optional.of(targetUser));
        when(userRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(authenticatedUser));

        userService.delete(1L, "owner@email.com");

        verify(userRepository).delete(targetUser);
    }

    @Test
    void shouldThrowExceptionWhenNonOwnerNonAdminTriesToUpdateUser() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Carlos atualizado");
        request.setCpf("12345678900");
        request.setEmail("carlos@novo.com");
        request.setPasswordHash("nova-senha");

        User targetUser = mock(User.class);
        when(targetUser.getId()).thenReturn(1L);

        User intruderUser = mock(User.class);
        when(intruderUser.getId()).thenReturn(2L);
        when(intruderUser.getUserType()).thenReturn(UserType.COMMON);

        when(userRepository.findById(1L)).thenReturn(Optional.of(targetUser));
        when(userRepository.findByEmail("intruder@email.com")).thenReturn(Optional.of(intruderUser));

        OnlyOwnerCanManageUserException exception = assertThrows(
                OnlyOwnerCanManageUserException.class,
                () -> userService.update(1L, request, "intruder@email.com")
        );

        assertEquals("Only the user owner or admin can manage this user", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
