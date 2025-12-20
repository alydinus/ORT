package kg.spring.ort.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "articles")
public class ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String html;

    @Builder.Default
    private Long views = 0L;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ArticleStatus status = ArticleStatus.PENDING;

    @ManyToOne
    private Category category;

    @ManyToMany
    private List<Tag> tags;

    @OneToMany
    @JoinColumn(name = "article_entity_id")
    private List<ReactionEntity> reactions;

    @OneToMany
    @JoinColumn(name = "article_entity_id")
    private List<CommentEntity> comments;

    private Long authorId;
    private LocalDateTime createdAt;
}
