package br.com.adoption.service;

import br.com.adoption.dto.response.EmailConfirmationResponse;
import br.com.adoption.entity.User;

public interface EmailVerificationService {
    String prepareEmailVerification(User user);
    void sendEmailVerification(User user, String token);
    EmailConfirmationResponse confirmEmail(String token);
    EmailConfirmationResponse resendConfirmation(String email);
}
