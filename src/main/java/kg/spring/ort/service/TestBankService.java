package kg.spring.ort.service;


import kg.spring.ort.dto.request.CreateQuestionRequest;
import kg.spring.ort.dto.request.UpdateQuestionRequest;
import kg.spring.ort.entity.Question;

import java.util.List;

public interface TestBankService {

    List<Question> getAllQuestions();

    Question getQuestionById(Long id);

    Question createQuestion(CreateQuestionRequest questionRequest);

    Question updateQuestion(Long id, UpdateQuestionRequest questionRequest);

    void deleteQuestion(Long id);

}
