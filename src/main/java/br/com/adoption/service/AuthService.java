package br.com.adoption.service;

import br.com.adoption.dto.request.LoginRequest;
import br.com.adoption.dto.response.EmailConfirmationResponse;
import br.com.adoption.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    EmailConfirmationResponse confirmEmail(String token);
    EmailConfirmationResponse resendEmailConfirmation(String email);
}
