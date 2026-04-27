package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateUserRequest;
import br.com.adoption.dto.request.PatchUserRequest;
import br.com.adoption.dto.request.UpdateUserRequest;
import br.com.adoption.dto.response.UserResponse;
import br.com.adoption.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;



@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User management endpoints")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(
            summary = "List users",
            description = "Returns paginated users. Accessible only by admin users. Supports filtering by name and email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Admin access required", content = @Content(schema = @Schema()))
    })
    public PagedModel<UserResponse> getAllUsers(
            @Parameter(description = "Filters users by name using partial case-insensitive match")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filters users by email using partial case-insensitive match")
            @RequestParam(required = false) String email,
            @ParameterObject
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return new PagedModel<>(userService.getAllUsers(pageable, name, email));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by id",
            description = "Returns a specific user by id. Accessible only by admin users"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Admin access required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema()))
    })
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    @Operation(
            summary = "Create user",
            description = "Creates a new common user account"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(schema = @Schema()))
    })
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.save(request);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Replace user",
            description = "Fully updates a user. Allowed for the resource owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can update the user", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema()))
    })
    public UserResponse updateUser(@PathVariable Long id,
                                   @Valid @RequestBody UpdateUserRequest request,
                                   @Parameter(hidden = true)
                                   Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return userService.update(id, request, userEmail);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Partially update user",
            description = "Partially updates a user. Allowed for the resource owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can update the user", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema()))
    })
    public UserResponse patchUser(@PathVariable Long id,
                                  @Valid @RequestBody PatchUserRequest request,
                                  @Parameter(hidden = true)
                                  Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return userService.patch(id, request, userEmail);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user",
            description = "Deletes a user. Allowed for the resource owner or admin"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can delete the user", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema()))
    })
    public UserResponse deleteUser(@PathVariable Long id,
                                   @Parameter(hidden = true)
                                   Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return userService.delete(id, userEmail);
    }
}
