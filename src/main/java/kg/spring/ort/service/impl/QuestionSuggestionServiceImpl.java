package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.CreateAnswerRequest;
import kg.spring.ort.dto.request.CreateQuestionSuggestionRequest;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.dto.response.QuestionSuggestionResponse;
import kg.spring.ort.entity.Answer;
import kg.spring.ort.entity.Question;
import kg.spring.ort.entity.QuestionSuggestion;
import kg.spring.ort.entity.QuestionSuggestionAnswer;
import kg.spring.ort.entity.QuestionSuggestionStatus;
import kg.spring.ort.entity.QuestionType;
import kg.spring.ort.exception.BadRequestException;
import kg.spring.ort.exception.NotFoundException;
import kg.spring.ort.mapper.QuestionSuggestionMapper;
import kg.spring.ort.repository.QuestionRepository;
import kg.spring.ort.repository.QuestionSuggestionRepository;
import kg.spring.ort.repository.UserRepository;
import kg.spring.ort.service.QuestionSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QuestionSuggestionServiceImpl implements QuestionSuggestionService {

    private final QuestionSuggestionRepository suggestionRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final QuestionSuggestionMapper mapper;

    @Override
    @Transactional
    public QuestionSuggestionResponse suggest(CreateQuestionSuggestionRequest request, String username) {
        var user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByUsername(username))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        validateQuestion(request.questionType(), request.answers().stream().filter(CreateAnswerRequest::isCorrect).count());

        QuestionSuggestion s = new QuestionSuggestion();
        s.setAuthor(user);
        s.setQuestionText(request.questionText().trim());
        s.setQuestionType(request.questionType());
        s.setPoints(request.points());
        s.setStatus(QuestionSuggestionStatus.PENDING);
        s.setCreatedAt(LocalDateTime.now());

        for (CreateAnswerRequest a : request.answers()) {
            QuestionSuggestionAnswer sa = new QuestionSuggestionAnswer();
            sa.setAnswerText(a.answerText().trim());
            sa.setCorrect(a.isCorrect());
            s.addAnswer(sa);
        }

        return mapper.toResponse(suggestionRepository.save(s));
    }

    @Override
    public PageResponse<QuestionSuggestionResponse> getMySuggestions(int page, int size, String username) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 50);
        var pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));

        var result = suggestionRepository.findByAuthor_UsernameOrderByIdDesc(username, pageable);
        var content = result.getContent().stream().map(mapper::toResponse).toList();

        return new PageResponse<>(content, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), result.isLast());
    }

    @Override
    public PageResponse<QuestionSuggestionResponse> getQueue(int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 50);
        var pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));

        var result = suggestionRepository.findByStatus(QuestionSuggestionStatus.PENDING, pageable);
        var content = result.getContent().stream().map(mapper::toResponse).toList();

        return new PageResponse<>(content, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), result.isLast());
    }

    @Override
    @Transactional
    public QuestionSuggestionResponse approve(Long id) {
        var s = suggestionRepository.findById(id).orElseThrow(() -> new NotFoundException("Предложение не найдено"));
        if (s.getStatus() != QuestionSuggestionStatus.PENDING) {
            throw new BadRequestException("Предложение уже обработано");
        }

        Question q = new Question();
        q.setQuestionText(s.getQuestionText());
        q.setQuestionType(s.getQuestionType());
        q.setPoints(s.getPoints());

        for (var a : s.getAnswers()) {
            Answer qa = new Answer();
            qa.setAnswerText(a.getAnswerText());
            qa.setCorrect(a.isCorrect());
            q.addAnswer(qa);
        }

        questionRepository.save(q);
        s.setStatus(QuestionSuggestionStatus.APPROVED);
        return mapper.toResponse(suggestionRepository.save(s));
    }

    @Override
    @Transactional
    public QuestionSuggestionResponse reject(Long id) {
        var s = suggestionRepository.findById(id).orElseThrow(() -> new NotFoundException("Предложение не найдено"));
        if (s.getStatus() != QuestionSuggestionStatus.PENDING) {
            throw new BadRequestException("Предложение уже обработано");
        }
        s.setStatus(QuestionSuggestionStatus.REJECTED);
        return mapper.toResponse(suggestionRepository.save(s));
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

