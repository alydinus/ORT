package kg.spring.ort.repository;

import kg.spring.ort.entity.QuestionSuggestion;
import kg.spring.ort.entity.QuestionSuggestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionSuggestionRepository extends JpaRepository<QuestionSuggestion, Long> {

    @EntityGraph(attributePaths = {"answers", "author"})
    Page<QuestionSuggestion> findByStatus(QuestionSuggestionStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"answers", "author"})
    Page<QuestionSuggestion> findByAuthor_UsernameOrderByIdDesc(String username, Pageable pageable);
}

