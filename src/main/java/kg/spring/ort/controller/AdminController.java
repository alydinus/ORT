package kg.spring.ort.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kg.spring.ort.entity.User;
import kg.spring.ort.service.AnalyticsService;
import kg.spring.ort.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final AnalyticsService analyticsService;

    @GetMapping("/users")
    @Operation(summary = "Get all users", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/users/{id}/lock")
    @Operation(summary = "Lock user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{id}/unlock")
    @Operation(summary = "Unlock user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/roles")
    @Operation(summary = "Assign role to user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> assignRole(@PathVariable Long userId, @RequestParam String roleName) {
        userService.assignRole(userId, roleName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get system analytics", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        return ResponseEntity.ok(analyticsService.getAnalytics());
    }
}
