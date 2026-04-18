package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateUserRequest;
import br.com.adoption.dto.response.UserResponse;
import br.com.adoption.entity.User;
import br.com.adoption.entity.UserType;
import br.com.adoption.mapper.UserMapper;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public UserResponse save(CreateUserRequest request) {
        User user = UserMapper.toEntity(request);

        user.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        user.setUserType(UserType.COMMON);

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }
}