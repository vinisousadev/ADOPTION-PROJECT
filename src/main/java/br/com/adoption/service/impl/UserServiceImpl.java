package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateUserRequest;
import br.com.adoption.dto.request.PatchUserRequest;
import br.com.adoption.dto.request.UpdateUserRequest;
import br.com.adoption.dto.response.UserResponse;
import br.com.adoption.entity.User;
import br.com.adoption.entity.UserType;
import br.com.adoption.exception.DuplicateUserEmailException;
import br.com.adoption.exception.InvalidFileUploadException;
import br.com.adoption.exception.OnlyOwnerCanManageUserException;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.mapper.UserMapper;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.EmailVerificationService;
import br.com.adoption.service.SupabaseStorageService;
import br.com.adoption.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final long MAX_PROFILE_PHOTO_SIZE = 4 * 1024 * 1024;
    private static final Map<String, String> ALLOWED_PROFILE_PHOTO_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SupabaseStorageService supabaseStorageService;
    private final EmailVerificationService emailVerificationService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           SupabaseStorageService supabaseStorageService,
                           EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.supabaseStorageService = supabaseStorageService;
        this.emailVerificationService = emailVerificationService;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll(Sort.by("id"));
        return UserMapper.toResponseList(users);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable, String name, String email) {
        return userRepository.findAll(buildUserFilterSpecification(name, email), pageable)
                .map(UserMapper::toResponse);
    }

    @Override
    public UserResponse getById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse getById(Long userId, String userEmail) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<User> authenticatedUser = isBlank(userEmail)
                ? Optional.empty()
                : userRepository.findByEmail(userEmail);

        if (authenticatedUser.isPresent() && isOwnerOrAdmin(targetUser, authenticatedUser.get())) {
            return UserMapper.toResponse(targetUser);
        }

        return UserMapper.toPublicResponse(targetUser);
    }

    @Override
    public UserResponse save(CreateUserRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(existingUser -> {
                    throw new DuplicateUserEmailException("Email already registered");
                });

        User user = UserMapper.toEntity(request);

        user.setRegistrationDate(OffsetDateTime.now());
        user.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        user.setUserType(UserType.COMMON);

        String confirmationToken = emailVerificationService.prepareEmailVerification(user);

        User savedUser = userRepository.save(user);

        emailVerificationService.sendEmailVerification(savedUser, confirmationToken);

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
        if (request.getRoleLabel() != null) {
            targetUser.setRoleLabel(request.getRoleLabel());
        }
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
        if (request.getRoleLabel() != null) {
            targetUser.setRoleLabel(request.getRoleLabel());
        }
        if (request.getPasswordHash() != null) {
            targetUser.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        }

        User updatedUser = userRepository.save(targetUser);
        return UserMapper.toResponse(updatedUser);
    }

    @Override
    public UserResponse uploadProfilePhoto(String userEmail, MultipartFile file) {
        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateProfilePhoto(file);

        String contentType = file.getContentType();
        String extension = ALLOWED_PROFILE_PHOTO_TYPES.get(contentType);

        try {
            String profilePhotoUrl = supabaseStorageService.uploadUserProfilePhoto(
                    authenticatedUser.getId(),
                    file.getBytes(),
                    contentType,
                    extension
            );

            authenticatedUser.setProfilePhotoUrl(profilePhotoUrl);
            User updatedUser = userRepository.save(authenticatedUser);
            return UserMapper.toResponse(updatedUser);
        } catch (IOException exception) {
            throw new InvalidFileUploadException("Could not read profile photo file");
        }
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
        if (!isOwnerOrAdmin(targetUser, authenticatedUser)) {
            throw new OnlyOwnerCanManageUserException("Only the user owner or admin can manage this user");
        }
    }

    private boolean isOwnerOrAdmin(User targetUser, User authenticatedUser) {
        boolean isAdmin = authenticatedUser.getUserType() == UserType.ADMIN;
        boolean isOwner = targetUser.getId().equals(authenticatedUser.getId());

        return isAdmin || isOwner;
    }

    private void validateProfilePhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileUploadException("Profile photo is required");
        }

        if (file.getSize() > MAX_PROFILE_PHOTO_SIZE) {
            throw new InvalidFileUploadException("Profile photo must be at most 4MB");
        }

        if (!ALLOWED_PROFILE_PHOTO_TYPES.containsKey(file.getContentType())) {
            throw new InvalidFileUploadException("Profile photo must be JPG, PNG or WEBP");
        }
    }

    private Specification<User> buildUserFilterSpecification(String name, String email) {
        return hasName(name).and(hasEmail(email));
    }

    private Specification<User> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                isBlank(name)
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("name")),
                        "%" + name.trim().toUpperCase() + "%"
                );
    }

    private Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
                isBlank(email)
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(
                        criteriaBuilder.upper(root.get("email")),
                        "%" + email.trim().toUpperCase() + "%"
                );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
