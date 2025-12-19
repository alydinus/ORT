package kg.spring.ort.repository;

import kg.spring.ort.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
    List<TestEntity> findByIsActiveTrue();
}
