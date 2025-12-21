package kg.spring.ort.repository;

import kg.spring.ort.entity.ContentCommentEntity;
import kg.spring.ort.entity.ContentTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentCommentRepository extends JpaRepository<ContentCommentEntity, Long> {
    Page<ContentCommentEntity> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(ContentTargetType targetType, Long targetId, Pageable pageable);

    long countByTargetTypeAndTargetId(ContentTargetType targetType, Long targetId);
}

