package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.CreateArticleRequest;
import kg.spring.ort.dto.request.UpdateArticleRequest;
import kg.spring.ort.entity.ArticleEntity;
import kg.spring.ort.entity.ArticleStatus;
import kg.spring.ort.entity.ReactionEntity;
import kg.spring.ort.exception.ArticleNotFoundException;
import kg.spring.ort.exception.NotFoundException;
import kg.spring.ort.repository.ArticleRepository;
import kg.spring.ort.repository.ReactionRepository;
import kg.spring.ort.repository.UserRepository;
import kg.spring.ort.service.ArticleService;
import kg.spring.ort.valueobj.ReactionValueObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;

    @Override
    public ArticleEntity suggestArticle(CreateArticleRequest request, String username) {
        var user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByUsername(username))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ArticleEntity entity = ArticleEntity.builder()
                .title(request.title())
                .content(request.content())
                .authorId(user.getId())
                .status(ArticleStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        return articleRepository.save(entity);
    }

    @Override
    public ArticleEntity createArticle(CreateArticleRequest request, String username) {
        var user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByUsername(username))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ArticleEntity entity = ArticleEntity.builder()
                .title(request.title())
                .content(request.content())
                .authorId(user.getId())
                .status(ArticleStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .build();
        return articleRepository.save(entity);
    }

    @Override
    public ArticleEntity getPublishedArticleById(Long id) {
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Статья не найдена"));
        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            throw new ArticleNotFoundException("Статья не найдена");
        }
        return article;
    }

    @Override
    public List<ArticleEntity> getPublishedArticles() {
        return articleRepository.findAllByStatus(ArticleStatus.PUBLISHED);
    }

    @Override
    public List<ArticleEntity> getArticlesForModeration() {
        return articleRepository.findAllByStatus(ArticleStatus.PENDING);
    }

    @Override
    public ArticleEntity updateArticle(Long id, UpdateArticleRequest request) {
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Статья не найдена"));
        article.setTitle(request.title());
        article.setContent(request.content());
        return articleRepository.save(article);
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public void addView(Long id) {
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Статья не найдена"));
        article.setViews(article.getViews() + 1);
        articleRepository.save(article);
    }

    @Override
    public void publishArticle(Long id) {
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Статья не найдена"));
        article.setStatus(ArticleStatus.PUBLISHED);
        articleRepository.save(article);
    }

    @Override
    public void hideArticle(Long id) {
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Статья не найдена"));
        article.setStatus(ArticleStatus.HIDDEN);
        articleRepository.save(article);
    }

    @Override
    @Transactional
    public void toggleLike(Long articleId, String username) {
        var user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByUsername(username))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Long userId = user.getId();

        var existing = reactionRepository.findByArticleEntityIdAndAuthorId(articleId, userId);
        if (existing.isPresent()) {
            reactionRepository.delete(existing.get());
            return;
        }

        reactionRepository.save(
                ReactionEntity.builder()
                        .articleEntityId(articleId)
                        .authorId(userId)
                        .reactionValue(ReactionValueObject.LIKE)
                        .build()
        );
    }
}
