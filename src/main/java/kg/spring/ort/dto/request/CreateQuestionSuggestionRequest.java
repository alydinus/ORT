package kg.spring.ort.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kg.spring.ort.entity.QuestionType;

import java.util.List;

public record CreateQuestionSuggestionRequest(
        @NotBlank String questionText,
        @NotNull QuestionType questionType,
        @NotNull @Positive Integer points,
        @NotEmpty List<@Valid CreateAnswerRequest> answers
) {
}

