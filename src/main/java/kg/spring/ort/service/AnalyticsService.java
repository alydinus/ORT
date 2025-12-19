package kg.spring.ort.service;

import kg.spring.ort.repository.ArticleRepository;
import kg.spring.ort.repository.TestRepository;
import kg.spring.ort.repository.TestResultRepository;
import kg.spring.ort.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final ArticleRepository articleRepository;
    private final TestResultRepository testResultRepository;

    public Map<String, Object> getAnalytics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalTests", testRepository.count());
        stats.put("totalArticles", articleRepository.count());
        stats.put("totalTestAttempts", testResultRepository.count());
        // More sophisticated stats can be added here (popular tests, etc)
        // For now adhering to MVP requirement: popular tests, stats of progress, count
        // users

        return stats;
    }
}
