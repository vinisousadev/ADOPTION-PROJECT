package br.com.adoption.mapper;

import br.com.adoption.dto.request.CreateUserRequest;
import br.com.adoption.dto.response.UserResponse;
import br.com.adoption.entity.User;
import br.com.adoption.entity.UserRoleLabel;

import java.util.List;

public class UserMapper {

    public static User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setCpf(request.getCpf());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setRoleLabel(request.getRoleLabel() != null ? request.getRoleLabel() : UserRoleLabel.PROTETOR);
        user.setPasswordHash(request.getPasswordHash());
        return user;
    }

    public static UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setCpf(user.getCpf());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setCity(user.getCity());
        response.setState(user.getState());
        response.setProfilePhotoUrl(user.getProfilePhotoUrl());
        response.setRoleLabel(user.getRoleLabel());
        response.setRegistrationDate(user.getRegistrationDate());
        response.setUserType(user.getUserType());
        response.setEmailVerified(user.isEmailVerified());
        return response;
    }

    public static UserResponse toPublicResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setCity(user.getCity());
        response.setState(user.getState());
        response.setProfilePhotoUrl(user.getProfilePhotoUrl());
        response.setRoleLabel(user.getRoleLabel());
        response.setRegistrationDate(user.getRegistrationDate());
        response.setUserType(user.getUserType());
        response.setEmailVerified(user.isEmailVerified());
        return response;
    }

    public static List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
                .map(UserMapper::toResponse)
                .toList();
    }
}
