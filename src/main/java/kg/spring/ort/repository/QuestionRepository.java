package kg.spring.ort.repository;

import kg.spring.ort.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @EntityGraph(attributePaths = {"answers"})
    Page<Question> findByQuestionTextContainingIgnoreCase(String query, Pageable pageable);
}
