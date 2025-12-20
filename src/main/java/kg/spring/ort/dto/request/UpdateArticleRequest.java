package kg.spring.ort.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateArticleRequest(
        @NotBlank String title,
        @NotBlank String content
) {
}
