package kg.spring.ort.controller;

import jakarta.validation.Valid;
import kg.spring.ort.dto.request.LoginRequest;
import kg.spring.ort.dto.request.RegisterRequest;
import kg.spring.ort.dto.response.RegisterResponse;
import kg.spring.ort.dto.response.TokenPair;
import kg.spring.ort.mapper.AuthMapper;
import kg.spring.ort.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthMapper authMapper;

    @PostMapping("/login")
    public ResponseEntity<TokenPair> login(@RequestBody @Valid LoginRequest req) {
        return new ResponseEntity<>(authService.login(req), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return new ResponseEntity<>(authMapper.toRegisterResponse(authService.register(request)),
                HttpStatus.CREATED);
    }

    @PostMapping("/confirm-registration")
    public ResponseEntity<TokenPair> confirmRegistration(
            @RequestParam String email,
            @RequestParam String otp) {
        return ResponseEntity.ok(authService.confirmRegistration(email, otp));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody kg.spring.ort.dto.request.ForgotPasswordRequest request) {
        authService.forgotPassword(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody kg.spring.ort.dto.request.ResetPasswordRequest request) {
        authService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok().build();
    }
}