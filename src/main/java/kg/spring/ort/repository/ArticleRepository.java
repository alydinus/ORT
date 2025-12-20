package kg.spring.ort.repository;

import kg.spring.ort.entity.ArticleEntity;
import kg.spring.ort.entity.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
    List<ArticleEntity> findAllByStatus(ArticleStatus status);
}
