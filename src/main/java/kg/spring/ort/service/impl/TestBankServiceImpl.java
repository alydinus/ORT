package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.CreateAnswerRequest;
import kg.spring.ort.dto.request.CreateQuestionRequest;
import kg.spring.ort.dto.request.UpdateAnswerRequest;
import kg.spring.ort.dto.request.UpdateQuestionRequest;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.entity.Answer;
import kg.spring.ort.entity.Question;
import kg.spring.ort.entity.QuestionType;
import kg.spring.ort.exception.BadRequestException;
import kg.spring.ort.exception.QuestionNotFoundException;
import kg.spring.ort.repository.QuestionRepository;
import kg.spring.ort.service.TestBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestBankServiceImpl implements TestBankService {

    private final QuestionRepository questionRepository;

    @Override
    public PageResponse<Question> getQuestionsPage(int page, int size, String query) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 50);

        var pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));

        var q = query == null ? null : query.trim();
        var result = (q == null || q.isEmpty())
                ? questionRepository.findAll(pageable)
                : questionRepository.findByQuestionTextContainingIgnoreCase(q, pageable);

        return new PageResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
    }

    @Override
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElseThrow(
                () -> new QuestionNotFoundException("Вопрос не найден: " + id)
        );
    }

    @Override
    public Question createQuestion(CreateQuestionRequest questionRequest) {
        validateQuestion(questionRequest.questionType(), questionRequest.answers().stream().filter(CreateAnswerRequest::isCorrect).count());

        Question question = new Question();
        question.setQuestionText(questionRequest.questionText().trim());
        question.setQuestionType(questionRequest.questionType());
        question.setPoints(questionRequest.points());

        for (CreateAnswerRequest answerRequest : questionRequest.answers()) {
            Answer answer = new Answer();
            answer.setAnswerText(answerRequest.answerText().trim());
            answer.setCorrect(answerRequest.isCorrect());
            question.addAnswer(answer);
        }

        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(Long id, UpdateQuestionRequest questionRequest) {
        validateQuestion(questionRequest.questionType(), questionRequest.answers().stream().filter(UpdateAnswerRequest::isCorrect).count());

        Question existingQuestion = questionRepository.findById(id).orElseThrow(
                () -> new QuestionNotFoundException("Вопрос не найден: " + id)
        );

        existingQuestion.setQuestionText(questionRequest.questionText().trim());
        existingQuestion.setQuestionType(questionRequest.questionType());
        existingQuestion.setPoints(questionRequest.points());
        existingQuestion.getAnswers().clear();

        for (UpdateAnswerRequest answerRequest : questionRequest.answers()) {
            Answer answer = new Answer();
            answer.setAnswerText(answerRequest.answerText().trim());
            answer.setCorrect(answerRequest.isCorrect());
            existingQuestion.addAnswer(answer);
        }

        return questionRepository.save(existingQuestion);
    }

    @Override
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new QuestionNotFoundException("Вопрос не найден: " + id);
        }
        questionRepository.deleteById(id);
    }

    private void validateQuestion(QuestionType questionType, long correctAnswersCount) {
        if (correctAnswersCount < 1) {
            throw new BadRequestException("Должен быть хотя бы один правильный ответ");
        }
        if (questionType == QuestionType.SINGLE && correctAnswersCount != 1) {
            throw new BadRequestException("Для вопроса с одним вариантом ответа должен быть ровно один правильный ответ");
        }
    }
}
