package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateUserRequest;
import br.com.adoption.dto.request.PatchUserRequest;
import br.com.adoption.dto.request.UpdateUserRequest;
import br.com.adoption.dto.response.UserResponse;
import br.com.adoption.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public PagedModel<UserResponse> getAllUsers(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return new PagedModel<>(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.save(request);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id,
                                   @Valid @RequestBody UpdateUserRequest request,
                                   Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return userService.update(id, request, userEmail);
    }

    @PatchMapping("/{id}")
    public UserResponse patchUser(@PathVariable Long id,
                                  @Valid @RequestBody PatchUserRequest request,
                                  Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return userService.patch(id, request, userEmail);
    }

    @DeleteMapping("/{id}")
    public UserResponse deleteUser(@PathVariable Long id,
                                   Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return userService.delete(id, userEmail);
    }
}
