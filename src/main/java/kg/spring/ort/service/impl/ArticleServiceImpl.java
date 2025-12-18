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

    public ArticleEntity createArticle(CreateArticleRequest request) {
        return articleRepository.save(
                ArticleEntity.builder()
                        .title(request.title())
                        .content(request.content())
                        .authorId(request.authorId())
                        .html(request.html())
                        .build()
        );
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
}
