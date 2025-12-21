package kg.spring.ort.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "question_suggestions")
public class QuestionSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    private String questionText;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    private Integer points;

    @Enumerated(EnumType.STRING)
    private QuestionSuggestionStatus status;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "suggestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionSuggestionAnswer> answers = new ArrayList<>();

    public void addAnswer(QuestionSuggestionAnswer answer) {
        answers.add(answer);
        answer.setSuggestion(this);
    }
}
