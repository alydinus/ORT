package kg.spring.ort.service;

import kg.spring.ort.dto.request.SubmitTestRequest;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.dto.response.TestResponse;
import kg.spring.ort.entity.TestResult;

public interface TestService {
    PageResponse<TestResponse> getAllActiveTests(int page, int size, String theme);

    TestResponse getTestById(Long id);

    TestResult submitTest(String username, Long testId, SubmitTestRequest request);

    TestResult submitTestLegacy(String username, Long testId, Integer score);
}
