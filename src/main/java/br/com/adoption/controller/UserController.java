package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateUserRequest;
import br.com.adoption.dto.response.UserResponse;
import br.com.adoption.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.save(request);
    }
}