package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.CreateAnswerRequest;
import kg.spring.ort.dto.request.CreateQuestionRequest;
import kg.spring.ort.dto.request.UpdateAnswerRequest;
import kg.spring.ort.dto.request.UpdateQuestionRequest;
import kg.spring.ort.entity.Answer;
import kg.spring.ort.entity.Question;
import kg.spring.ort.exception.QuestionNotFoundException;
import kg.spring.ort.repository.QuestionRepository;
import kg.spring.ort.service.TestBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestBankServiceImpl implements TestBankService {

    private final QuestionRepository questionRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElseThrow(
                () -> new QuestionNotFoundException("Question not found with id: " + id)
        );
    }

    public Question createQuestion(CreateQuestionRequest questionRequest) {

        Question question = new Question();
        question.setQuestionText(questionRequest.questionText());

        for (CreateAnswerRequest answerRequest : questionRequest.answers()) {
            Answer answer = new Answer();
            answer.setAnswerText(answerRequest.answerText());
            answer.setCorrect(answerRequest.isCorrect());
            question.addAnswer(answer);
        }

        return questionRepository.save(question);
    }

    public Question updateQuestion(Long id, UpdateQuestionRequest questionRequest) {

        Question existingQuestion = questionRepository.findById(id).orElseThrow(
                () -> new QuestionNotFoundException("Question not found with id: " + id)
        );

        existingQuestion.setQuestionText(questionRequest.questionText());
        existingQuestion.getAnswers().clear();

        for (UpdateAnswerRequest answerRequest : questionRequest.answers()) {
            Answer answer = new Answer();
            answer.setAnswerText(answerRequest.answerText());
            answer.setCorrect(answerRequest.isCorrect());
            existingQuestion.addAnswer(answer);
        }

        return questionRepository.save(existingQuestion);
    }

    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new QuestionNotFoundException("Question not found with id: " + id);
        }
        questionRepository.deleteById(id);
    }
}
