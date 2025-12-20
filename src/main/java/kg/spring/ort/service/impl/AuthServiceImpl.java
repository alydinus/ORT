package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.LoginRequest;
import kg.spring.ort.dto.request.RegisterRequest;
import kg.spring.ort.dto.response.TokenPair;
import kg.spring.ort.entity.User;
import kg.spring.ort.repository.RoleRepository;
import kg.spring.ort.repository.UserRepository;
import kg.spring.ort.service.AuthService;
import kg.spring.ort.service.EmailService;
import kg.spring.ort.service.OtpService;
import kg.spring.ort.util.JwtGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final EmailService emailService;
    private final OtpService otpService;

    @Transactional
    public User register(RegisterRequest request) {
        Optional<User> existingUserByEmail = userRepository.findByEmail(request.email());
        if (existingUserByEmail.isPresent()) {
            User existing = existingUserByEmail.get();
            if (existing.isEnabled()) {
                throw new RuntimeException("User with this email is already registered and confirmed");
            } else {
                String otp = otpService.generateOtp(existing.getEmail());
                emailService.sendEmail(existing.getEmail(), "ORT Platform Registration", "Your new OTP is: " + otp);
                return existing;
            }
        }

        Optional<User> existingUserByUsername = userRepository.findByUsername(request.username());
        if (existingUserByUsername.isPresent()) {
            throw new RuntimeException("Username is already taken");
        }

        User user = User.builder()
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .username(request.username())
                .build();

        roleRepository.findByName("ROLE_USER").ifPresent(role -> user.getRoles().add(role));
        user.setEnabled(false);
        User savedUser = userRepository.save(user);

        String otp = otpService.generateOtp(user.getEmail());
        emailService.sendEmail(user.getEmail(), "ORT Platform Registration", "Your OTP is: " + otp);

        return savedUser;
    }

    public TokenPair confirmRegistration(String email, String otp) {
        if (!otpService.validateOtp(email, otp)) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        return new TokenPair(
                jwtGenerator.generateAccessToken(user.getUsername()),
                jwtGenerator.generateRefreshToken(user.getUsername()));
    }

    public TokenPair login(LoginRequest request) {
        log.info("Attempting login for username: {}", request.username());
        Optional<User> byUsername = userRepository.findByUsername(request.username());
        if (byUsername.isPresent()) {
            User user = byUsername.get();
            if (!user.isEnabled()) {
                throw new RuntimeException("Account not verified. Please check your email for OTP.");
            }
            if (user.isLocked()) {
                throw new RuntimeException("Account is locked. Please contact support.");
            }
            if (passwordEncoder.matches(request.password(), user.getPassword())) {
                return new TokenPair(
                        jwtGenerator.generateAccessToken(user.getUsername()),
                        jwtGenerator.generateRefreshToken(user.getUsername()));
            } else {
                throw new RuntimeException("Invalid password");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public TokenPair refresh(String refreshToken) {
        if (jwtGenerator.isRefresh(refreshToken) && jwtGenerator.notExpiredRefresh(refreshToken)) {
            String username = jwtGenerator.extractUsernameFromRefresh(refreshToken);
            return new TokenPair(
                    jwtGenerator.generateAccessToken(username),
                    jwtGenerator.generateRefreshToken(username));
        } else {
            throw new RuntimeException("Expired or invalid refresh token");
        }
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate a 15-min token (using existing OtpService or new logic.
        // Let's use OtpService but for password reset, or just generate a UUID and
        // store in Redis)
        // For simplicity and consistency, let's generate a random string token and
        // store in Redis.
        String token = java.util.UUID.randomUUID().toString();
        // Prefix with "pwd_reset:"
        otpService.saveOtp("pwd_reset:" + token, email); // Reusing OTP service to map Token -> Email

        emailService.sendEmail(email, "Password Reset Request",
                "Click or copy this token to reset password: " + token);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        String email = otpService.getOtp("pwd_reset:" + token); // Reusing getOtp to get value (email)
        if (email == null) {
            throw new RuntimeException("Invalid or expired reset token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Invalidate token
        otpService.deleteOtp("pwd_reset:" + token);
    }
}
