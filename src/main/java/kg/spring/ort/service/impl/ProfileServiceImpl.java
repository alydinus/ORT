package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.ChangePasswordRequest;
import kg.spring.ort.dto.request.UpdateProfileRequest;
import kg.spring.ort.dto.response.MeResponse;
import kg.spring.ort.entity.Role;
import kg.spring.ort.exception.BadRequestException;
import kg.spring.ort.exception.ConflictException;
import kg.spring.ort.exception.NotFoundException;
import kg.spring.ort.repository.UserRepository;
import kg.spring.ort.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public MeResponse getMe(String username) {
        var user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        var roles = user.getRoles().stream().map(Role::getName).toList();
        return new MeResponse(user.getId(), user.getUsername(), user.getEmail(), roles);
    }

    @Override
    @Transactional
    public MeResponse updateProfile(String username, UpdateProfileRequest request) {
        var user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!user.getUsername().equals(request.getUsername())) {
            var existing = userRepository.findByUsername(request.getUsername());
            if (existing.isPresent()) {
                throw new ConflictException("Имя пользователя уже занято");
            }
            user.setUsername(request.getUsername());
        }

        if (!user.getEmail().equals(request.getEmail())) {
            var existing = userRepository.findByEmail(request.getEmail());
            if (existing.isPresent()) {
                throw new ConflictException("Почта уже используется");
            }
            user.setEmail(request.getEmail());
        }

        userRepository.save(user);
        var roles = user.getRoles().stream().map(Role::getName).toList();
        return new MeResponse(user.getId(), user.getUsername(), user.getEmail(), roles);
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Текущий пароль неверный");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
