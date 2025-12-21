package kg.spring.ort.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kg.spring.ort.dto.request.CreateTestRequest;
import kg.spring.ort.dto.request.SetTestQuestionsRequest;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.dto.response.TestAdminResponse;
import kg.spring.ort.service.TestAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/v1/api/moderation/tests")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_MODERATOR','ROLE_ADMIN')")
public class TestAdminController {

    private final TestAdminService testAdminService;

    @GetMapping
    @Operation(summary = "Получить список тестов (модерация, постранично)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PageResponse<TestAdminResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String theme,
            @RequestParam(required = false) String tag
    ) {
        return ResponseEntity.ok(testAdminService.getAll(page, size, query, theme, tag));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить тест по ID (модерация)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TestAdminResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(testAdminService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Создать тест", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TestAdminResponse> create(@Valid @RequestBody CreateTestRequest request,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        var created = testAdminService.create(request, userDetails.getUsername());
        return ResponseEntity.created(URI.create("/v1/api/moderation/tests/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить тест", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TestAdminResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody kg.spring.ort.dto.request.UpdateTestRequest request) {
        return ResponseEntity.ok(testAdminService.update(id, request));
    }

    @PutMapping("/{id}/questions")
    @Operation(summary = "Заменить вопросы в тесте", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TestAdminResponse> setQuestions(@PathVariable Long id,
                                                          @Valid @RequestBody SetTestQuestionsRequest request) {
        return ResponseEntity.ok(testAdminService.setQuestions(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить тест", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        testAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
