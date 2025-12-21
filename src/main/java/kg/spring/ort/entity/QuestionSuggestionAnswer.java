package kg.spring.ort.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "question_suggestion_answers")
public class QuestionSuggestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String answerText;

    private boolean isCorrect;

    @ManyToOne(optional = false)
    @JoinColumn(name = "suggestion_id")
    private QuestionSuggestion suggestion;
}

