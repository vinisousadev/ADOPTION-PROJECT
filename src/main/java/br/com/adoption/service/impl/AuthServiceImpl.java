package br.com.adoption.service.impl;

import br.com.adoption.dto.request.LoginRequest;
import br.com.adoption.dto.response.EmailConfirmationResponse;
import br.com.adoption.dto.response.LoginResponse;
import br.com.adoption.entity.User;
import br.com.adoption.exception.EmailNotVerifiedException;
import br.com.adoption.exception.InvalidCredentialsException;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.AuthService;
import br.com.adoption.service.EmailVerificationService;
import br.com.adoption.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailVerificationService = emailVerificationService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Confirm your email before logging in");
        }

        String token = jwtService.generateToken(user);

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setUserType(user.getUserType());
        response.setMessage("Login successful");
        response.setToken(token);

        return response;
    }

    @Override
    public EmailConfirmationResponse confirmEmail(String token) {
        return emailVerificationService.confirmEmail(token);
    }

    @Override
    public EmailConfirmationResponse resendEmailConfirmation(String email) {
        return emailVerificationService.resendConfirmation(email);
    }
}
