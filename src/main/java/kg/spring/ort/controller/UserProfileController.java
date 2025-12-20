package kg.spring.ort.controller;

import jakarta.validation.Valid;
import kg.spring.ort.dto.request.ChangePasswordRequest;
import kg.spring.ort.dto.request.UpdateProfileRequest;
import kg.spring.ort.dto.response.ApiMessageResponse;
import kg.spring.ort.dto.response.MeResponse;
import kg.spring.ort.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Principal principal) {
        return ResponseEntity.ok(profileService.getMe(principal.getName()));
    }

    @PatchMapping("/me")
    public ResponseEntity<MeResponse> updateMe(Principal principal, @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(principal.getName(), request));
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<ApiMessageResponse> changePassword(Principal principal,
                                                            @Valid @RequestBody ChangePasswordRequest request) {
        profileService.changePassword(principal.getName(), request);
        return ResponseEntity.ok(new ApiMessageResponse("Пароль обновлён"));
    }
}
