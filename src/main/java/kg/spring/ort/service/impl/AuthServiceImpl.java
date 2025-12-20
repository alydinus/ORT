package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.LoginRequest;
import kg.spring.ort.dto.request.RegisterRequest;
import kg.spring.ort.dto.response.TokenPair;
import kg.spring.ort.entity.User;
import kg.spring.ort.exception.BadRequestException;
import kg.spring.ort.exception.ConflictException;
import kg.spring.ort.exception.ForbiddenException;
import kg.spring.ort.exception.NotFoundException;
import kg.spring.ort.exception.UnauthorizedException;
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
                throw new ConflictException("Пользователь с этой почтой уже зарегистрирован");
            }
            String otp = otpService.generateOtp(existing.getEmail());
            emailService.sendEmail(existing.getEmail(), "ORT Platform Registration", "Ваш новый код подтверждения: " + otp);
            return existing;
        }

        Optional<User> existingUserByUsername = userRepository.findByUsername(request.username());
        if (existingUserByUsername.isPresent()) {
            throw new ConflictException("Имя пользователя уже занято");
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
        emailService.sendEmail(user.getEmail(), "ORT Platform Registration", "Ваш код подтверждения: " + otp);

        return savedUser;
    }

    public TokenPair confirmRegistration(String email, String otp) {
        if (!otpService.validateOtp(email, otp)) {
            throw new BadRequestException("Неверный или просроченный код подтверждения");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        user.setEnabled(true);
        userRepository.save(user);

        return new TokenPair(
                jwtGenerator.generateAccessToken(user.getUsername()),
                jwtGenerator.generateRefreshToken(user.getUsername()));
    }

    public TokenPair login(LoginRequest request) {
        log.info("Попытка входа: {}", request.username());
        Optional<User> byUsername = userRepository.findByUsername(request.username());
        if (byUsername.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        User user = byUsername.get();
        if (!user.isEnabled()) {
            throw new ForbiddenException("Аккаунт не подтверждён. Проверьте почту");
        }
        if (user.isLocked()) {
            throw new ForbiddenException("Аккаунт заблокирован. Обратитесь в поддержку");
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Неверный пароль");
        }

        return new TokenPair(
                jwtGenerator.generateAccessToken(user.getUsername()),
                jwtGenerator.generateRefreshToken(user.getUsername()));
    }

    @Override
    public TokenPair refresh(String refreshToken) {
        if (jwtGenerator.isRefresh(refreshToken) && jwtGenerator.notExpiredRefresh(refreshToken)) {
            String username = jwtGenerator.extractUsernameFromRefresh(refreshToken);
            return new TokenPair(
                    jwtGenerator.generateAccessToken(username),
                    jwtGenerator.generateRefreshToken(username));
        }
        throw new UnauthorizedException("Токен обновления просрочен или недействителен");
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        String token = java.util.UUID.randomUUID().toString();
        otpService.saveOtp("pwd_reset:" + token, email);

        emailService.sendEmail(email, "ORT Platform", "Токен для сброса пароля: " + token);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        String email = otpService.getOtp("pwd_reset:" + token);
        if (email == null) {
            throw new BadRequestException("Токен сброса пароля недействителен или просрочен");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpService.deleteOtp("pwd_reset:" + token);
    }
}
