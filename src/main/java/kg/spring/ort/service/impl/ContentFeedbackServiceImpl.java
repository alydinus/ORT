package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.CreateContentCommentRequest;
import kg.spring.ort.dto.request.SetReactionRequest;
import kg.spring.ort.dto.response.ContentCommentResponse;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.dto.response.ReactionsSummaryResponse;
import kg.spring.ort.entity.ContentCommentEntity;
import kg.spring.ort.entity.ContentReactionEntity;
import kg.spring.ort.entity.ContentTargetType;
import kg.spring.ort.exception.BadRequestException;
import kg.spring.ort.exception.NotFoundException;
import kg.spring.ort.repository.ArticleRepository;
import kg.spring.ort.repository.ContentCommentRepository;
import kg.spring.ort.repository.ContentReactionRepository;
import kg.spring.ort.repository.QuestionRepository;
import kg.spring.ort.repository.TestRepository;
import kg.spring.ort.repository.UserRepository;
import kg.spring.ort.valueobj.ReactionValueObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContentFeedbackServiceImpl implements kg.spring.ort.service.ContentFeedbackService {

    private final ContentReactionRepository reactionRepository;
    private final ContentCommentRepository commentRepository;
    private final UserRepository userRepository;

    private final ArticleRepository articleRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

    @Override
    public ReactionsSummaryResponse getReactions(ContentTargetType targetType, Long targetId, String usernameOrNull) {
        ensureTargetExists(targetType, targetId);

        long likes = reactionRepository.countByTargetTypeAndTargetIdAndReactionValue(targetType, targetId, ReactionValueObject.LIKE);
        long dislikes = reactionRepository.countByTargetTypeAndTargetIdAndReactionValue(targetType, targetId, ReactionValueObject.DISLIKE);

        ReactionValueObject my = null;
        if (usernameOrNull != null && !usernameOrNull.isBlank()) {
            var user = userRepository.findByUsername(usernameOrNull)
                    .or(() -> userRepository.findByEmail(usernameOrNull))
                    .orElse(null);
            if (user != null) {
                my = reactionRepository.findUserReaction(targetType, targetId, user.getId()).orElse(null);
            }
        }

        return new ReactionsSummaryResponse(likes, dislikes, my);
    }

    @Override
    @Transactional
    public ReactionsSummaryResponse setReaction(ContentTargetType targetType, Long targetId, String username, SetReactionRequest request) {
        ensureTargetExists(targetType, targetId);

        var user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (request == null || request.value() == null) {
            throw new BadRequestException("Реакция не указана");
        }

        var existing = reactionRepository.findByTargetTypeAndTargetIdAndAuthorId(targetType, targetId, user.getId());
        LocalDateTime now = LocalDateTime.now();

        if (existing.isEmpty()) {
            ContentReactionEntity e = new ContentReactionEntity();
            e.setTargetType(targetType);
            e.setTargetId(targetId);
            e.setAuthorId(user.getId());
            e.setReactionValue(request.value());
            e.setCreatedAt(now);
            e.setUpdatedAt(now);
            reactionRepository.save(e);
        } else {
            ContentReactionEntity e = existing.get();
            if (e.getReactionValue() == request.value()) {
                reactionRepository.delete(e);
            } else {
                e.setReactionValue(request.value());
                e.setUpdatedAt(now);
                reactionRepository.save(e);
            }
        }

        return getReactions(targetType, targetId, username);
    }

    @Override
    public PageResponse<ContentCommentResponse> getComments(ContentTargetType targetType, Long targetId, int page, int size) {
        ensureTargetExists(targetType, targetId);

        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 50);
        var pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        var result = commentRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(targetType, targetId, pageable);

        var content = result.getContent().stream().map(c -> {
            String username = userRepository.findById(c.getAuthorId()).map(u -> u.getUsername()).orElse(null);
            return new ContentCommentResponse(c.getId(), c.getAuthorId(), username, c.getText(), c.getCreatedAt());
        }).toList();

        return new PageResponse<>(content, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), result.isLast());
    }

    @Override
    @Transactional
    public ContentCommentResponse createComment(ContentTargetType targetType, Long targetId, String username, CreateContentCommentRequest request) {
        ensureTargetExists(targetType, targetId);

        var user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (request == null || request.text() == null || request.text().trim().isEmpty()) {
            throw new BadRequestException("Текст комментария не должен быть пустым");
        }

        LocalDateTime now = LocalDateTime.now();

        ContentCommentEntity c = new ContentCommentEntity();
        c.setTargetType(targetType);
        c.setTargetId(targetId);
        c.setAuthorId(user.getId());
        c.setText(request.text().trim());
        c.setCreatedAt(now);
        c.setUpdatedAt(now);

        commentRepository.save(c);
        return new ContentCommentResponse(c.getId(), user.getId(), user.getUsername(), c.getText(), c.getCreatedAt());
    }

    private void ensureTargetExists(ContentTargetType targetType, Long targetId) {
        if (targetId == null) {
            throw new BadRequestException("Идентификатор не указан");
        }

        boolean exists = switch (targetType) {
            case ARTICLE -> articleRepository.existsById(targetId);
            case TEST -> testRepository.existsById(targetId);
            case QUESTION -> questionRepository.existsById(targetId);
        };

        if (!exists) {
            throw new NotFoundException("Объект не найден");
        }
    }
}

