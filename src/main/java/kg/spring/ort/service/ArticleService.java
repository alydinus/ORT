package kg.spring.ort.service;


import kg.spring.ort.dto.request.CreateArticleRequest;
import kg.spring.ort.dto.request.UpdateArticleRequest;
import kg.spring.ort.entity.ArticleEntity;

import java.util.List;

public interface ArticleService {
    ArticleEntity createArticle(CreateArticleRequest request);
    ArticleEntity getArticleById(Long id);
    List<ArticleEntity> getAllArticles();
    ArticleEntity updateArticle(Long id, UpdateArticleRequest request);
    void deleteArticle(Long id);
}
