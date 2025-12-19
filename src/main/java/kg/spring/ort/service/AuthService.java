package kg.spring.ort.service;

import kg.spring.ort.dto.request.LoginRequest;
import kg.spring.ort.dto.request.RegisterRequest;
import kg.spring.ort.dto.response.TokenPair;
import kg.spring.ort.entity.User;

public interface AuthService {
    User register(RegisterRequest request);

    TokenPair login(LoginRequest request);

    TokenPair confirmRegistration(String email, String otp);

    void forgotPassword(String email);

    void resetPassword(String token, String newPassword);

    TokenPair refresh(String refreshToken);

}
