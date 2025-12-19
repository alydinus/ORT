package kg.spring.ort.service;

import kg.spring.ort.entity.TestEntity;
import kg.spring.ort.entity.TestResult;

import java.util.List;

public interface TestService {
    List<TestEntity> getAllActiveTests();

    TestEntity getTestById(Long id);

    TestResult submitTest(String username, Long testId, Integer score);
}
