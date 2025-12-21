package kg.spring.ort.repository;

import kg.spring.ort.entity.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface QuestionWithAnswersRepository extends JpaRepository<Question, Long> {

    @EntityGraph(attributePaths = {"answers"})
    List<Question> findAllByIdIn(Collection<Long> ids);
}
