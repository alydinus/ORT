package kg.spring.ort.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kg.spring.ort.valueobj.ReactionValueObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "content_reactions")
public class ContentReactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ContentTargetType targetType;

    private Long targetId;

    private Long authorId;

    @Enumerated(EnumType.STRING)
    private ReactionValueObject reactionValue;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

