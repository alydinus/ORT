package kg.spring.ort.dto.request;

public record CreateArticleRequest(
        String title,
        String content,
        Long authorId,
        String html
) {
}
