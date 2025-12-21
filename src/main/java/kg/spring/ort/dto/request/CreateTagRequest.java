package kg.spring.ort.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateTagRequest(
        @NotBlank String name
) {
}

