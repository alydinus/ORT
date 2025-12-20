package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.SubmitTestRequest;
import kg.spring.ort.dto.response.AnswerResponse;
import kg.spring.ort.dto.response.QuestionResponse;
import kg.spring.ort.dto.response.TestResponse;
import kg.spring.ort.entity.TestEntity;
import kg.spring.ort.entity.TestResult;
import kg.spring.ort.entity.User;
import kg.spring.ort.exception.BadRequestException;
import kg.spring.ort.exception.NotFoundException;
import kg.spring.ort.repository.TestRepository;
import kg.spring.ort.repository.TestResultRepository;
import kg.spring.ort.repository.UserRepository;
import kg.spring.ort.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;

    @Override
    public List<TestResponse> getAllActiveTests() {
        return testRepository.findByIsActiveTrue().stream().map(this::toResponse).toList();
    }

    @Override
    public TestResponse getTestById(Long id) {
        TestEntity test = testRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Тест не найден"));
        return toResponse(test);
    }

    @Override
    @Transactional
    public TestResult submitTest(String username, Long testId, SubmitTestRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        TestEntity test = testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException("Тест не найден"));

        if (request.answers() == null || request.answers().isEmpty()) {
            throw new BadRequestException("Ответы не переданы");
        }

        int total = test.getQuestions().size();
        if (total == 0) {
            throw new BadRequestException("В тесте нет вопросов");
        }

        int correct = 0;
        for (var q : test.getQuestions()) {
            Long selectedAnswerId = request.answers().get(q.getId());
            if (selectedAnswerId == null) {
                continue;
            }
            var answer = q.getAnswers().stream().filter(a -> a.getId().equals(selectedAnswerId)).findFirst();
            if (answer.isPresent() && answer.get().isCorrect()) {
                correct++;
            }
        }

        int score = Math.round((correct / (float) total) * 100);

        TestResult result = TestResult.builder()
                .user(user)
                .test(test)
                .score(score)
                .date(LocalDateTime.now())
                .build();

        return testResultRepository.save(result);
    }

    private TestResponse toResponse(TestEntity test) {
        var questions = test.getQuestions().stream()
                .map(q -> new QuestionResponse(
                        q.getId(),
                        q.getQuestionText(),
                        q.getQuestionType(),
                        q.getPoints(),
                        q.getAnswers().stream()
                                .map(a -> new AnswerResponse(a.getId(), a.getAnswerText()))
                                .toList()
                ))
                .toList();

        return new TestResponse(
                test.getId(),
                test.getTitle(),
                test.getDescription(),
                test.getDurationMinutes(),
                test.isActive(),
                questions
        );
    }
}
