package kg.spring.ort.controller;

import kg.spring.ort.dto.response.MeResponse;
import kg.spring.ort.entity.Role;
import kg.spring.ort.exception.NotFoundException;
import kg.spring.ort.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Principal principal) {
        String username = principal.getName();
        var user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        var roles = user.getRoles().stream().map(Role::getName).toList();
        return ResponseEntity.ok(new MeResponse(user.getId(), user.getUsername(), user.getEmail(), roles));
    }
}
