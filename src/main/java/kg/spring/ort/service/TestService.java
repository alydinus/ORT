package kg.spring.ort.service;

import kg.spring.ort.dto.request.SubmitTestRequest;
import kg.spring.ort.dto.response.TestResponse;
import kg.spring.ort.entity.TestResult;

import java.util.List;

public interface TestService {
    List<TestResponse> getAllActiveTests();

    TestResponse getTestById(Long id);

    TestResult submitTest(String username, Long testId, SubmitTestRequest request);
}
