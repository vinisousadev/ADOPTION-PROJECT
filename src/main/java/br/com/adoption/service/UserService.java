package br.com.adoption.service;

import br.com.adoption.dto.request.CreateUserRequest;
import br.com.adoption.dto.request.PatchUserRequest;
import br.com.adoption.dto.request.UpdateUserRequest;
import br.com.adoption.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    Page<UserResponse> getAllUsers(Pageable pageable, String name, String email);
    UserResponse getById(Long userId);
    UserResponse save(CreateUserRequest request);
    UserResponse update(Long userId, UpdateUserRequest request, String userEmail);
    UserResponse patch(Long userId, PatchUserRequest request, String userEmail);
    UserResponse delete(Long userId, String userEmail);
}
