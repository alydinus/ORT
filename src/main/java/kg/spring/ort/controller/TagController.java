package kg.spring.ort.controller;

import jakarta.validation.Valid;
import kg.spring.ort.dto.request.CreateTagRequest;
import kg.spring.ort.dto.response.TagResponse;
import kg.spring.ort.service.TagService;
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
@RequestMapping("/v1/api/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAll() {
        return ResponseEntity.ok(tagService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MODERATOR','ROLE_ADMIN')")
    public ResponseEntity<TagResponse> create(@Valid @RequestBody CreateTagRequest request) {
        return new ResponseEntity<>(tagService.create(request), HttpStatus.CREATED);
    }
}

