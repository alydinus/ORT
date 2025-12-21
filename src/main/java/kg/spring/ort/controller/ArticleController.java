package kg.spring.ort.controller;

import jakarta.validation.Valid;
import kg.spring.ort.dto.request.CreateArticleRequest;
import kg.spring.ort.dto.request.UpdateArticleRequest;
import kg.spring.ort.dto.response.ArticleResponse;
import kg.spring.ort.mapper.ArticleMapper;
import kg.spring.ort.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/v1/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable Long id) {
        return new ResponseEntity<>(
                articleMapper.toResponse(articleService.getPublishedArticleById(id)),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllArticles() {
        return new ResponseEntity<>(
                articleService.getPublishedArticles()
                        .stream()
                        .map(articleMapper::toResponse)
                        .toList(),
                HttpStatus.OK);
    }

    @PostMapping("/suggest")
    public ResponseEntity<ArticleResponse> suggestArticle(@RequestBody @Valid CreateArticleRequest request,
                                                         Principal principal) {
        return new ResponseEntity<>(
                articleMapper.toResponse(articleService.suggestArticle(request, principal.getName())),
                HttpStatus.CREATED);
    }

    @PostMapping("/moderation/create")
    @PreAuthorize("hasAnyAuthority('ROLE_MODERATOR','ROLE_ADMIN')")
    public ResponseEntity<ArticleResponse> createDirect(@RequestBody @Valid CreateArticleRequest request,
                                                        Principal principal) {
        var created = articleService.suggestArticle(request, principal.getName());
        articleService.publishArticle(created.getId());
        return new ResponseEntity<>(articleMapper.toResponse(articleService.getPublishedArticleById(created.getId())), HttpStatus.CREATED);
    }

    @GetMapping("/moderation/queue")
    public ResponseEntity<?> moderationQueue() {
        return new ResponseEntity<>(
                articleService.getArticlesForModeration()
                        .stream()
                        .map(articleMapper::toResponse)
                        .toList(),
                HttpStatus.OK);
    }

    @PutMapping("/moderation/{id}/publish")
    public ResponseEntity<Void> publishArticle(@PathVariable Long id) {
        articleService.publishArticle(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/moderation/{id}/hide")
    public ResponseEntity<Void> hideArticle(@PathVariable Long id) {
        articleService.hideArticle(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable Long id,
                                                         @RequestBody @Valid UpdateArticleRequest request) {
        return new ResponseEntity<>(
                articleMapper.toResponse(articleService.updateArticle(id, request)),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> addView(@PathVariable Long id) {
        articleService.addView(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> toggleLike(@PathVariable Long id, Principal principal) {
        articleService.toggleLike(id, principal.getName());
        return ResponseEntity.ok().build();
    }
}
