package kg.spring.ort.controller;

import kg.spring.ort.dto.response.ContentCommentResponse;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.dto.response.ReactionsSummaryResponse;
import kg.spring.ort.entity.ContentTargetType;
import kg.spring.ort.service.ContentFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/v1/content")
public class PublicContentFeedbackController {

    private final ContentFeedbackService contentFeedbackService;

    @GetMapping("/{type}/{id}/reactions")
    public ResponseEntity<ReactionsSummaryResponse> getReactions(@PathVariable("type") ContentTargetType type,
                                                                 @PathVariable("id") Long id,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails == null ? null : userDetails.getUsername();
        return ResponseEntity.ok(contentFeedbackService.getReactions(type, id, username));
    }

    @GetMapping("/{type}/{id}/comments")
    public ResponseEntity<PageResponse<ContentCommentResponse>> getComments(@PathVariable("type") ContentTargetType type,
                                                                            @PathVariable("id") Long id,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(contentFeedbackService.getComments(type, id, page, size));
    }
}

