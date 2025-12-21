package kg.spring.ort.service;


import kg.spring.ort.dto.request.CreateQuestionRequest;
import kg.spring.ort.dto.request.UpdateQuestionRequest;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.entity.Question;

public interface TestBankService {

    PageResponse<Question> getQuestionsPage(int page, int size, String query);

    Question getQuestionById(Long id);

    Question createQuestion(CreateQuestionRequest questionRequest);

    Question updateQuestion(Long id, UpdateQuestionRequest questionRequest);

    void deleteQuestion(Long id);
}
