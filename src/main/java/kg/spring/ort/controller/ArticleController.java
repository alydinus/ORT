package kg.spring.ort.controller;

import kg.spring.ort.dto.request.CreateArticleRequest;
import kg.spring.ort.dto.request.UpdateArticleRequest;
import kg.spring.ort.dto.response.ArticleResponse;
import kg.spring.ort.mapper.ArticleMapper;
import kg.spring.ort.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable Long id) {
        return new ResponseEntity<>(
                articleMapper.toResponse(articleService.getArticleById(id)),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllArticles() {
        return new ResponseEntity<>(
                articleService.getAllArticles()
                        .stream()
                        .map(articleMapper::toResponse)
                        .toList(),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(@RequestBody CreateArticleRequest request) {
        return new ResponseEntity<>(
                articleMapper.toResponse(articleService.createArticle(request)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable Long id, @RequestBody UpdateArticleRequest request) {
        return new ResponseEntity<>(
                articleMapper.toResponse(articleService.updateArticle(id, request)),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
