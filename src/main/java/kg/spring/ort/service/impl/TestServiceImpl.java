package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.SubmitTestRequest;
import kg.spring.ort.dto.response.AnswerResponse;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.dto.response.QuestionResponse;
import kg.spring.ort.dto.response.TestResponse;
import kg.spring.ort.entity.Tag;
import kg.spring.ort.entity.TestEntity;
import kg.spring.ort.entity.TestResult;
import kg.spring.ort.entity.User;
import kg.spring.ort.exception.BadRequestException;
import kg.spring.ort.exception.NotFoundException;
import kg.spring.ort.repository.QuestionWithAnswersRepository;
import kg.spring.ort.repository.TestRepository;
import kg.spring.ort.repository.TestResultRepository;
import kg.spring.ort.repository.UserRepository;
import kg.spring.ort.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;
    private final QuestionWithAnswersRepository questionWithAnswersRepository;

    @Override
    public PageResponse<TestResponse> getAllActiveTests(int page, int size, String theme) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        var pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.ASC, "title"));

        var result = (theme == null || theme.trim().isEmpty())
                ? testRepository.findByIsActiveTrue(pageable)
                : testRepository.findByIsActiveTrueAndTheme_NameIgnoreCase(theme.trim().toLowerCase(java.util.Locale.ROOT), pageable);

        var content = result.getContent().stream().map(this::toLightResponse).toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TestResponse getTestById(Long id) {
        TestEntity test = testRepository.findWithQuestionsById(id)
                .orElseThrow(() -> new NotFoundException("Тест не найден"));

        List<Long> questionIds = test.getQuestions().stream().map(q -> q.getId()).toList();
        List<kg.spring.ort.entity.Question> questions = questionIds.isEmpty()
                ? List.of()
                : questionWithAnswersRepository.findAllByIdIn(questionIds);

        return toFullResponse(test, questions);
    }

    @Override
    @Transactional
    public TestResult submitTest(String username, Long testId, SubmitTestRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        TestEntity test = testRepository.findWithQuestionsById(testId)
                .orElseThrow(() -> new NotFoundException("Тест не найден"));

        List<Long> questionIds = test.getQuestions().stream().map(q -> q.getId()).toList();
        List<kg.spring.ort.entity.Question> questions = questionIds.isEmpty()
                ? List.of()
                : questionWithAnswersRepository.findAllByIdIn(questionIds);

        if (request.answers() == null || request.answers().isEmpty()) {
            throw new BadRequestException("Ответы не переданы");
        }

        int total = questions.size();
        if (total == 0) {
            throw new BadRequestException("В тесте нет вопросов");
        }

        int correct = 0;
        for (kg.spring.ort.entity.Question q : questions) {
            var selectedAnswerIds = request.answers().get(q.getId());
            if (selectedAnswerIds == null || selectedAnswerIds.isEmpty()) {
                continue;
            }

            var correctIds = q.getAnswers().stream()
                    .filter(a -> a.isCorrect())
                    .map(a -> a.getId())
                    .collect(java.util.stream.Collectors.toSet());

            var selectedIds = new HashSet<>(selectedAnswerIds);

            if (selectedIds.equals(correctIds)) {
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

    @Override
    @Transactional
    public TestResult submitTestLegacy(String username, Long testId, Integer score) {
        if (score == null) {
            throw new BadRequestException("Результат не передан");
        }
        if (score < 0 || score > 100) {
            throw new BadRequestException("Результат должен быть от 0 до 100");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        TestEntity test = testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException("Тест не найден"));

        TestResult result = TestResult.builder()
                .user(user)
                .test(test)
                .score(score)
                .date(LocalDateTime.now())
                .build();

        return testResultRepository.save(result);
    }

    private TestResponse toLightResponse(TestEntity test) {
        return new TestResponse(
                test.getId(),
                test.getTitle(),
                test.getDescription(),
                test.getDurationMinutes(),
                test.isActive(),
                test.getTheme() != null ? test.getTheme().getName() : null,
                test.getAuthor() != null ? test.getAuthor().getUsername() : null,
                test.getTags().stream().map(Tag::getName).sorted().toList(),
                List.of()
        );
    }

    private TestResponse toFullResponse(TestEntity test, List<kg.spring.ort.entity.Question> questions) {
        var responseQuestions = questions.stream()
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
                test.getTheme() != null ? test.getTheme().getName() : null,
                test.getAuthor() != null ? test.getAuthor().getUsername() : null,
                test.getTags().stream().map(Tag::getName).sorted().toList(),
                responseQuestions
        );
    }
}
