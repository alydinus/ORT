package kg.spring.ort.controller;

import jakarta.validation.Valid;
import kg.spring.ort.dto.request.CreateQuestionSuggestionRequest;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.dto.response.QuestionSuggestionResponse;
import kg.spring.ort.service.QuestionSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/questions")
public class QuestionSuggestionController {

    private final QuestionSuggestionService questionSuggestionService;

    @PostMapping("/suggest")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<QuestionSuggestionResponse> suggest(@Valid @RequestBody CreateQuestionSuggestionRequest request,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(questionSuggestionService.suggest(request, userDetails.getUsername()), HttpStatus.CREATED);
    }

    @GetMapping("/my-suggestions")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<PageResponse<QuestionSuggestionResponse>> mySuggestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(questionSuggestionService.getMySuggestions(page, size, userDetails.getUsername()));
    }

    @GetMapping("/moderation/queue")
    @PreAuthorize("hasAnyAuthority('ROLE_MODERATOR','ROLE_ADMIN')")
    public ResponseEntity<PageResponse<QuestionSuggestionResponse>> queue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(questionSuggestionService.getQueue(page, size));
    }

    @PostMapping("/moderation/{id}/approve")
    @PreAuthorize("hasAnyAuthority('ROLE_MODERATOR','ROLE_ADMIN')")
    public ResponseEntity<QuestionSuggestionResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(questionSuggestionService.approve(id));
    }

    @PostMapping("/moderation/{id}/reject")
    @PreAuthorize("hasAnyAuthority('ROLE_MODERATOR','ROLE_ADMIN')")
    public ResponseEntity<QuestionSuggestionResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(questionSuggestionService.reject(id));
    }
}

