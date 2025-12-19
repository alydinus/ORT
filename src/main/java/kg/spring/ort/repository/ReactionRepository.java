package kg.spring.ort.repository;

import kg.spring.ort.entity.ReactionEntity;
import kg.spring.ort.valueobj.ReactionValueObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<ReactionEntity, Long> {
    Optional<ReactionEntity> findByArticleEntityIdAndAuthorId(Long articleId, Long authorId);
}
