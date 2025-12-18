package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.LoginRequest;
import kg.spring.ort.dto.request.RegisterRequest;
import kg.spring.ort.dto.response.TokenPair;
import kg.spring.ort.entity.Role;
import kg.spring.ort.entity.User;
import kg.spring.ort.repository.RoleRepository;
import kg.spring.ort.repository.UserRepository;
import kg.spring.ort.service.AuthService;
import kg.spring.ort.util.JwtGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;

    @Transactional
    public User register(RegisterRequest request) {
        User user = User.builder()
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .username(request.username())
                .build();

        roleRepository.findByName("user").ifPresent(role -> user.getRoles().add(role));
        return userRepository.save(user);
    }

    public TokenPair login(LoginRequest request) {
        Optional<User> byUsername = userRepository.findByUsername(request.username());
        if (byUsername.isPresent()) {
            User user = byUsername.get();
            if (passwordEncoder.matches(request.password(), user.getPassword())) {
                return new TokenPair(
                        jwtGenerator.generateAccessToken(user.getUsername()),
                        jwtGenerator.generateRefreshToken(user.getUsername())
                );
            } else {
                throw new RuntimeException("Invalid password");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public TokenPair refresh(String refreshToken) {
        if (jwtGenerator.isRefresh(refreshToken) && jwtGenerator.notExpiredRefresh(refreshToken)) {
            String username = jwtGenerator.extractUsernameFromRefresh(refreshToken);
            return new TokenPair(
                    jwtGenerator.generateAccessToken(username),
                    jwtGenerator.generateRefreshToken(username)
            );
        } else {
            throw new RuntimeException("Expired or invalid refresh token");
        }
    }
}
