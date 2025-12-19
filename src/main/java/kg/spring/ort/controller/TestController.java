package kg.spring.ort.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kg.spring.ort.entity.TestEntity;
import kg.spring.ort.entity.TestResult;
import kg.spring.ort.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping
    @Operation(summary = "Get all active tests")
    public ResponseEntity<List<TestEntity>> getAllTests() {
        return ResponseEntity.ok(testService.getAllActiveTests());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @Operation(summary = "Get test by ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TestEntity> getTestById(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getTestById(id));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @Operation(summary = "Submit test results", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TestResult> submitTest(@PathVariable Long id,
            @RequestParam Integer score,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(testService.submitTest(userDetails.getUsername(), id, score));
    }
}
