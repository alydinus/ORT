package kg.spring.ort.service;

import kg.spring.ort.dto.request.CreateQuestionSuggestionRequest;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.dto.response.QuestionSuggestionResponse;

public interface QuestionSuggestionService {
    QuestionSuggestionResponse suggest(CreateQuestionSuggestionRequest request, String username);

    PageResponse<QuestionSuggestionResponse> getMySuggestions(int page, int size, String username);

    PageResponse<QuestionSuggestionResponse> getQueue(int page, int size);

    QuestionSuggestionResponse approve(Long id);

    QuestionSuggestionResponse reject(Long id);
}

