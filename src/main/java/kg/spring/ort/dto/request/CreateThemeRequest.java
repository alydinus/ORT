package kg.spring.ort.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateThemeRequest(
        @NotBlank String name
) {
}

