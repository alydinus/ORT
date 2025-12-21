package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.CreateTestRequest;
import kg.spring.ort.dto.request.SetTestQuestionsRequest;
import kg.spring.ort.dto.request.UpdateTestRequest;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.dto.response.TestAdminResponse;
import kg.spring.ort.entity.Question;
import kg.spring.ort.entity.Tag;
import kg.spring.ort.entity.TestEntity;
import kg.spring.ort.entity.TestTheme;
import kg.spring.ort.exception.BadRequestException;
import kg.spring.ort.exception.NotFoundException;
import kg.spring.ort.repository.QuestionRepository;
import kg.spring.ort.repository.TagRepository;
import kg.spring.ort.repository.TestRepository;
import kg.spring.ort.repository.ThemeRepository;
import kg.spring.ort.repository.UserRepository;
import kg.spring.ort.service.TestAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TestAdminServiceImpl implements TestAdminService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ThemeRepository themeRepository;

    @Override
    public PageResponse<TestAdminResponse> getAll(int page, int size, String query, String theme, String tag) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        var pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));

        var normalizedQuery = normalize(query);
        var normalizedTheme = normalize(theme);
        var normalizedTag = normalize(tag);

        Specification<TestEntity> spec = Specification.where(null);

        if (normalizedQuery != null) {
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("title")), "%" + normalizedQuery + "%"));
        }
        if (normalizedTheme != null) {
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.join("theme").get("name")), "%" + normalizedTheme + "%"));
        }
        if (normalizedTag != null) {
            spec = spec.and((root, q, cb) -> {
                q.distinct(true);
                var join = root.joinSet("tags");
                return cb.like(cb.lower(join.get("name")), "%" + normalizedTag + "%");
            });
        }

        var result = testRepository.findAll(spec, pageable);
        var content = result.getContent().stream().map(this::toResponse).toList();

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
    public TestAdminResponse getById(Long id) {
        return toResponse(testRepository.findById(id).orElseThrow(() -> new NotFoundException("Тест не найден")));
    }

    @Override
    @Transactional
    public TestAdminResponse create(CreateTestRequest request, String authorUsername) {
        if (request.questionIds() != null && request.questionIds().isEmpty()) {
            throw new BadRequestException("Список вопросов не может быть пустым");
        }

        var author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        TestEntity test = new TestEntity();
        test.setTitle(request.title().trim());
        test.setDescription(request.description());
        test.setDurationMinutes(request.durationMinutes());
        test.setActive(request.isActive() == null || request.isActive());
        test.setTheme(resolveTheme(request.theme()));
        test.setAuthor(author);

        applyTags(test, request.tags());

        if (request.questionIds() != null) {
            List<Question> questions = questionRepository.findAllById(request.questionIds());
            if (questions.size() != request.questionIds().size()) {
                throw new BadRequestException("Некоторые вопросы не найдены");
            }
            test.setQuestions(new ArrayList<>(questions));
        }

        TestEntity saved = testRepository.save(test);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public TestAdminResponse update(Long id, UpdateTestRequest request) {
        TestEntity test = testRepository.findById(id).orElseThrow(() -> new NotFoundException("Тест не найден"));
        test.setTitle(request.title().trim());
        test.setDescription(request.description());
        test.setDurationMinutes(request.durationMinutes());
        test.setActive(request.isActive());
        test.setTheme(resolveTheme(request.theme()));

        applyTags(test, request.tags());

        return toResponse(testRepository.save(test));
    }

    @Override
    @Transactional
    public TestAdminResponse setQuestions(Long id, SetTestQuestionsRequest request) {
        TestEntity test = testRepository.findById(id).orElseThrow(() -> new NotFoundException("Тест не найден"));

        List<Question> questions = questionRepository.findAllById(request.questionIds());
        if (questions.size() != request.questionIds().size()) {
            throw new BadRequestException("Некоторые вопросы не найдены");
        }

        test.getQuestions().clear();
        test.getQuestions().addAll(questions);

        return toResponse(testRepository.save(test));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!testRepository.existsById(id)) {
            throw new NotFoundException("Тест не найден");
        }
        testRepository.deleteById(id);
    }

    private TestTheme resolveTheme(String themeName) {
        String name = normalize(themeName);
        if (name == null) {
            return null;
        }
        return themeRepository.findByName(name)
                .orElseThrow(() -> new BadRequestException("Тема не найдена. Создайте её в справочнике тем"));
    }

    private void applyTags(TestEntity test, List<String> tagNames) {
        test.getTags().clear();
        if (tagNames == null) {
            return;
        }

        for (String raw : tagNames) {
            String name = normalize(raw);
            if (name == null) {
                continue;
            }

            Tag tag = tagRepository.findByName(name)
                    .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()));
            test.getTags().add(tag);
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        var v = value.trim();
        if (v.isEmpty()) {
            return null;
        }
        return v.toLowerCase(Locale.ROOT);
    }

    private TestAdminResponse toResponse(TestEntity test) {
        return new TestAdminResponse(
                test.getId(),
                test.getTitle(),
                test.getDescription(),
                test.getDurationMinutes(),
                test.isActive(),
                test.getTheme() != null ? test.getTheme().getName() : null,
                test.getAuthor() != null ? test.getAuthor().getUsername() : null,
                test.getTags().stream().map(Tag::getName).sorted().toList(),
                test.getQuestions().stream().map(Question::getId).toList()
        );
    }
}
