package kg.spring.ort.service.impl;

import kg.spring.ort.dto.request.CreateArticleRequest;
import kg.spring.ort.dto.request.UpdateArticleRequest;
import kg.spring.ort.entity.ArticleEntity;
import kg.spring.ort.exception.ArticleNotFoundException;
import kg.spring.ort.repository.ArticleRepository;
import kg.spring.ort.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final kg.spring.ort.repository.ReactionRepository reactionRepository;
    private final kg.spring.ort.repository.UserRepository userRepository;

    public ArticleEntity createArticle(CreateArticleRequest request) {
        return articleRepository.save(
                ArticleEntity.builder()
                        .title(request.title())
                        .content(request.content())
                        .authorId(request.authorId())
                        .html(request.html())
                        .build());
    }

    public ArticleEntity getArticleById(Long id) {
        return articleRepository.findById(id).orElseThrow(() -> new ArticleNotFoundException("Article not found"));
    }

    public List<ArticleEntity> getAllArticles() {
        return articleRepository.findAll();
    }

    public ArticleEntity updateArticle(Long id, UpdateArticleRequest request) {
        ArticleEntity articleEntity = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found"));
        articleEntity.setTitle(request.title());
        articleEntity.setContent(request.content());
        articleEntity.setAuthorId(request.authorId());
        articleEntity.setHtml(request.html());
        return articleRepository.save(articleEntity);
    }

    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public void addView(Long id) {
        ArticleEntity article = getArticleById(id);
        article.setViews(article.getViews() + 1);
        articleRepository.save(article);
    }

    @Override
    public void publishArticle(Long id) {
        ArticleEntity article = getArticleById(id);
        article.setPublished(true);
        articleRepository.save(article);
    }

    @Override
    public void hideArticle(Long id) {
        ArticleEntity article = getArticleById(id);
        article.setPublished(false);
        articleRepository.save(article);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void toggleLike(Long articleId, String username) {
        kg.spring.ort.entity.User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByUsername(username))
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();

        java.util.Optional<kg.spring.ort.entity.ReactionEntity> existing = reactionRepository
                .findByArticleEntityIdAndAuthorId(articleId, userId);
        if (existing.isPresent()) {
            reactionRepository.delete(existing.get());
        } else {
            ArticleEntity article = getArticleById(articleId);
            kg.spring.ort.entity.ReactionEntity reaction = kg.spring.ort.entity.ReactionEntity.builder()
                    .articleEntityId(articleId)
                    .reactionValue(kg.spring.ort.valueobj.ReactionValueObject.LIKE)
                    .authorId(userId)
                    .build();

            if (article.getReactions() == null) {
                article.setReactions(new java.util.ArrayList<>());
            }
            article.getReactions().add(reaction);

            reactionRepository.save(reaction);
            articleRepository.save(article);
        }
    }
}
