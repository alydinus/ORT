package kg.spring.ort.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public record SubmitTestRequest(@NotEmpty Map<Long, Long> answers) {
}

