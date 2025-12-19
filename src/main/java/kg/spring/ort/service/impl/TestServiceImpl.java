package kg.spring.ort.service.impl;

import kg.spring.ort.entity.TestEntity;
import kg.spring.ort.entity.TestResult;
import kg.spring.ort.entity.User;
import kg.spring.ort.repository.TestRepository;
import kg.spring.ort.repository.TestResultRepository;
import kg.spring.ort.repository.UserRepository;
import kg.spring.ort.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;

    @Override
    public List<TestEntity> getAllActiveTests() {
        return testRepository.findByIsActiveTrue();
    }

    @Override
    public TestEntity getTestById(Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found"));
    }

    @Override
    @Transactional
    public TestResult submitTest(String username, Long testId, Integer score) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        TestEntity test = getTestById(testId);

        TestResult result = TestResult.builder()
                .user(user)
                .test(test)
                .score(score)
                .date(LocalDateTime.now())
                .build();

        return testResultRepository.save(result);
    }
}
