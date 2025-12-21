package kg.spring.ort.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record UpdateTestRequest(
        @NotBlank String title,
        String description,
        String theme,
        List<String> tags,
        @NotNull @Positive Integer durationMinutes,
        @NotNull Boolean isActive
) {
}
