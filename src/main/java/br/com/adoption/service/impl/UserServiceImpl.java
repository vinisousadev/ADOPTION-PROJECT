package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateUserRequest;
import br.com.adoption.dto.request.PatchUserRequest;
import br.com.adoption.dto.request.UpdateUserRequest;
import br.com.adoption.dto.response.UserResponse;
import br.com.adoption.entity.User;
import br.com.adoption.entity.UserType;
import br.com.adoption.exception.OnlyOwnerCanManageUserException;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.mapper.UserMapper;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll(Sort.by("id"));
        return UserMapper.toResponseList(users);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toResponse);
    }

    @Override
    public UserResponse getById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse save(CreateUserRequest request) {
        User user = UserMapper.toEntity(request);

        user.setRegistrationDate(LocalDateTime.now());
        user.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        user.setUserType(UserType.COMMON);

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse update(Long userId, UpdateUserRequest request, String userEmail) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateOwnerOrAdmin(targetUser, authenticatedUser);

        targetUser.setName(request.getName());
        targetUser.setCpf(request.getCpf());
        targetUser.setPhone(request.getPhone());
        targetUser.setEmail(request.getEmail());
        targetUser.setCity(request.getCity());
        targetUser.setState(request.getState());
        targetUser.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));

        User updatedUser = userRepository.save(targetUser);
        return UserMapper.toResponse(updatedUser);
    }

    @Override
    public UserResponse patch(Long userId, PatchUserRequest request, String userEmail) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateOwnerOrAdmin(targetUser, authenticatedUser);

        if (request.getName() != null) {
            targetUser.setName(request.getName());
        }
        if (request.getCpf() != null) {
            targetUser.setCpf(request.getCpf());
        }
        if (request.getPhone() != null) {
            targetUser.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            targetUser.setEmail(request.getEmail());
        }
        if (request.getCity() != null) {
            targetUser.setCity(request.getCity());
        }
        if (request.getState() != null) {
            targetUser.setState(request.getState());
        }
        if (request.getPasswordHash() != null) {
            targetUser.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        }

        User updatedUser = userRepository.save(targetUser);
        return UserMapper.toResponse(updatedUser);
    }

    @Override
    public UserResponse delete(Long userId, String userEmail) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateOwnerOrAdmin(targetUser, authenticatedUser);

        userRepository.delete(targetUser);
        return UserMapper.toResponse(targetUser);
    }

    private void validateOwnerOrAdmin(User targetUser, User authenticatedUser) {
        boolean isAdmin = authenticatedUser.getUserType() == UserType.ADMIN;
        boolean isOwner = targetUser.getId().equals(authenticatedUser.getId());

        if (!isAdmin && !isOwner) {
            throw new OnlyOwnerCanManageUserException("Only the user owner or admin can manage this user");
        }
    }
}
