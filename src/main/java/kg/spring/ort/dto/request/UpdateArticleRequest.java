package kg.spring.ort.dto.request;

public record UpdateArticleRequest(
        String title,
        String content,
        Long authorId,
        String html
) {
}
