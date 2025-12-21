package kg.spring.ort.service;

import kg.spring.ort.dto.request.CreateArticleRequest;
import kg.spring.ort.dto.request.UpdateArticleRequest;
import kg.spring.ort.entity.ArticleEntity;

import java.util.List;

public interface ArticleService {
    ArticleEntity suggestArticle(CreateArticleRequest request, String username);

    ArticleEntity createArticle(CreateArticleRequest request, String username);

    ArticleEntity getPublishedArticleById(Long id);

    List<ArticleEntity> getPublishedArticles();

    List<ArticleEntity> getArticlesForModeration();

    ArticleEntity updateArticle(Long id, UpdateArticleRequest request);

    void deleteArticle(Long id);

    void addView(Long id);

    void publishArticle(Long id);

    void hideArticle(Long id);

    void toggleLike(Long articleId, String username);
}
