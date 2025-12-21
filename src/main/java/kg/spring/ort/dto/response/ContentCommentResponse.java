package kg.spring.ort.dto.response;

import java.time.LocalDateTime;

public record ContentCommentResponse(
        Long id,
        Long authorId,
        String authorUsername,
        String text,
        LocalDateTime createdAt
) {
}

