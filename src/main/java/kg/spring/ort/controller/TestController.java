package kg.spring.ort.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kg.spring.ort.dto.request.SubmitTestRequest;
import kg.spring.ort.dto.response.TestResponse;
import kg.spring.ort.dto.response.TestResultResponse;
import kg.spring.ort.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping
    @Operation(summary = "Получить список активных тестов")
    public ResponseEntity<List<TestResponse>> getAllTests() {
        return ResponseEntity.ok(testService.getAllActiveTests());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @Operation(summary = "Получить тест по ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TestResponse> getTestById(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getTestById(id));
    }

    @PostMapping(value = "/{id}/submit", params = "score")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @Operation(summary = "Отправить результат (устаревший формат)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TestResultResponse> submitTestLegacy(@PathVariable Long id,
                                                               @RequestParam Integer score,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        var request = new SubmitTestRequest(Map.of());
        var result = testService.submitTest(userDetails.getUsername(), id, request);
        result.setScore(score);
        return ResponseEntity.ok(new TestResultResponse(
                result.getId(),
                result.getTest().getId(),
                result.getTest().getTitle(),
                result.getScore(),
                result.getDate()
        ));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @Operation(summary = "Отправить ответы и получить результат", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TestResultResponse> submitTest(@PathVariable Long id,
                                                         @Valid @RequestBody SubmitTestRequest request,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        var result = testService.submitTest(userDetails.getUsername(), id, request);
        return ResponseEntity.ok(new TestResultResponse(
                result.getId(),
                result.getTest().getId(),
                result.getTest().getTitle(),
                result.getScore(),
                result.getDate()
        ));
    }
}
