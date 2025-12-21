package kg.spring.ort.controller;

import jakarta.validation.Valid;
import kg.spring.ort.dto.request.CreateContentCommentRequest;
import kg.spring.ort.dto.request.SetReactionRequest;
import kg.spring.ort.dto.response.ContentCommentResponse;
import kg.spring.ort.dto.response.ReactionsSummaryResponse;
import kg.spring.ort.entity.ContentTargetType;
import kg.spring.ort.service.ContentFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/content")
public class ContentFeedbackController {

    private final ContentFeedbackService contentFeedbackService;

    @PostMapping("/{type}/{id}/reactions")
    public ResponseEntity<ReactionsSummaryResponse> setReaction(@PathVariable("type") ContentTargetType type,
                                                                @PathVariable("id") Long id,
                                                                @Valid @RequestBody SetReactionRequest request,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(contentFeedbackService.setReaction(type, id, userDetails.getUsername(), request));
    }

    @PostMapping("/{type}/{id}/comments")
    public ResponseEntity<ContentCommentResponse> createComment(@PathVariable("type") ContentTargetType type,
                                                                @PathVariable("id") Long id,
                                                                @Valid @RequestBody CreateContentCommentRequest request,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(contentFeedbackService.createComment(type, id, userDetails.getUsername(), request), HttpStatus.CREATED);
    }
}

