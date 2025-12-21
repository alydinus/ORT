package kg.spring.ort.mapper;

import kg.spring.ort.dto.response.QuestionSuggestionAnswerResponse;
import kg.spring.ort.dto.response.QuestionSuggestionResponse;
import kg.spring.ort.entity.QuestionSuggestion;
import kg.spring.ort.entity.QuestionSuggestionAnswer;
import org.springframework.stereotype.Component;

@Component
public class QuestionSuggestionMapper {

    public QuestionSuggestionResponse toResponse(QuestionSuggestion s) {
        return new QuestionSuggestionResponse(
                s.getId(),
                s.getAuthor() != null ? s.getAuthor().getUsername() : null,
                s.getQuestionText(),
                s.getQuestionType() != null ? s.getQuestionType().name() : null,
                s.getPoints(),
                s.getStatus() != null ? s.getStatus().name() : null,
                s.getCreatedAt(),
                s.getAnswers().stream().map(this::toAnswer).toList()
        );
    }

    public QuestionSuggestionAnswerResponse toAnswer(QuestionSuggestionAnswer a) {
        return new QuestionSuggestionAnswerResponse(a.getId(), a.getAnswerText(), a.isCorrect());
    }
}

