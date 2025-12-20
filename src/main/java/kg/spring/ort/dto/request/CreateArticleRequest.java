package kg.spring.ort.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateArticleRequest(
        @NotBlank String title,
        @NotBlank String content
) {
}
