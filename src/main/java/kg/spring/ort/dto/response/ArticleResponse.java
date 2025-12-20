package kg.spring.ort.dto.response;

import java.time.LocalDateTime;

public record ArticleResponse(
        Long id,
        String title,
        String content,
        String html,
        String status,
        Long authorId,
        LocalDateTime createdAt
) {
}
