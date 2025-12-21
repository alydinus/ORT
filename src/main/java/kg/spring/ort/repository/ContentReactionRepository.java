package kg.spring.ort.repository;

import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import kg.spring.ort.valueobj.ReactionValueObject;
import kg.spring.ort.entity.ContentTargetType;
import kg.spring.ort.entity.ContentReactionEntity;

public interface ContentReactionRepository extends JpaRepository<ContentReactionEntity, Long> {

    Optional<ContentReactionEntity> findByTargetTypeAndTargetIdAndAuthorId(ContentTargetType targetType, Long targetId, Long authorId);

    long countByTargetTypeAndTargetIdAndReactionValue(ContentTargetType targetType, Long targetId, ReactionValueObject reactionValue);

    @Query("select r.reactionValue from ContentReactionEntity r where r.targetType = :targetType and r.targetId = :targetId and r.authorId = :authorId")
    Optional<ReactionValueObject> findUserReaction(
            @Param("targetType") ContentTargetType targetType,
            @Param("targetId") Long targetId,
            @Param("authorId") Long authorId
    );
}