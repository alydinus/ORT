package kg.spring.ort.repository;

import kg.spring.ort.entity.TestTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<TestTheme, Long> {
    Optional<TestTheme> findByName(String name);
}
