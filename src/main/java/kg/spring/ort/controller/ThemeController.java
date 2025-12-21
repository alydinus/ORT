package kg.spring.ort.controller;

import jakarta.validation.Valid;
import kg.spring.ort.dto.request.CreateThemeRequest;
import kg.spring.ort.dto.response.ThemeResponse;
import kg.spring.ort.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/themes")
public class ThemeController {

    private final ThemeService themeService;

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getAll() {
        return ResponseEntity.ok(themeService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MODERATOR','ROLE_ADMIN')")
    public ResponseEntity<ThemeResponse> create(@Valid @RequestBody CreateThemeRequest request) {
        return new ResponseEntity<>(themeService.create(request), HttpStatus.CREATED);
    }
}

