package br.com.adoption.service;

import br.com.adoption.dto.request.LoginRequest;
import br.com.adoption.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}