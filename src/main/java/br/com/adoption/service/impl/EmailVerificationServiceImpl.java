package br.com.adoption.service.impl;

import br.com.adoption.dto.response.EmailConfirmationResponse;
import br.com.adoption.entity.User;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.EmailDeliveryService;
import br.com.adoption.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final int TOKEN_BYTES = 32;
    private static final int EXPIRATION_HOURS = 24;

    private final UserRepository userRepository;
    private final EmailDeliveryService emailDeliveryService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String frontendUrl;

    public EmailVerificationServiceImpl(UserRepository userRepository,
                                        EmailDeliveryService emailDeliveryService,
                                        @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
        this.userRepository = userRepository;
        this.emailDeliveryService = emailDeliveryService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public String prepareEmailVerification(User user) {
        String token = generateToken();

        user.setEmailVerified(false);
        user.setEmailVerificationTokenHash(hashToken(token));
        user.setEmailVerificationExpiresAt(OffsetDateTime.now().plusHours(EXPIRATION_HOURS));

        return token;
    }

    @Override
    public void sendEmailVerification(User user, String token) {
        emailDeliveryService.sendEmailConfirmation(user, buildConfirmationUrl(token));
    }

    @Override
    public EmailConfirmationResponse confirmEmail(String token) {
        User user = userRepository.findByEmailVerificationTokenHash(hashToken(token))
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired email confirmation token"));

        if (user.getEmailVerificationExpiresAt() == null ||
                user.getEmailVerificationExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new ResourceNotFoundException("Invalid or expired email confirmation token");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationTokenHash(null);
        user.setEmailVerificationExpiresAt(null);
        userRepository.save(user);

        return new EmailConfirmationResponse("Email confirmed successfully", true);
    }

    @Override
    public EmailConfirmationResponse resendConfirmation(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEmailVerified()) {
            return new EmailConfirmationResponse("Email is already confirmed", true);
        }

        String token = prepareEmailVerification(user);
        User savedUser = userRepository.save(user);
        sendEmailVerification(savedUser, token);

        return new EmailConfirmationResponse("Confirmation email sent", false);
    }

    private String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private String buildConfirmationUrl(String token) {
        String baseUrl = frontendUrl.endsWith("/")
                ? frontendUrl.substring(0, frontendUrl.length() - 1)
                : frontendUrl;

        return baseUrl + "/confirm-email?token=" + token;
    }
}
