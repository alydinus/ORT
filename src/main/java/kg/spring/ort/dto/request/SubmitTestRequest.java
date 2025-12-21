package kg.spring.ort.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

public record SubmitTestRequest(@NotEmpty Map<Long, List<Long>> answers) {
}

