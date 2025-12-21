package kg.spring.ort.repository;

import kg.spring.ort.entity.TestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long>, JpaSpecificationExecutor<TestEntity> {

    @EntityGraph(attributePaths = {"tags", "author", "theme"})
    Page<TestEntity> findByIsActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"tags", "author", "theme"})
    Page<TestEntity> findByIsActiveTrueAndTheme_NameIgnoreCase(String themeName, Pageable pageable);

    @EntityGraph(attributePaths = {"tags", "author", "theme", "questions"})
    Optional<TestEntity> findWithQuestionsById(Long id);
}
