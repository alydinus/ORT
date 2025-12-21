package kg.spring.ort.service;

import kg.spring.ort.dto.request.CreateTestRequest;
import kg.spring.ort.dto.request.SetTestQuestionsRequest;
import kg.spring.ort.dto.request.UpdateTestRequest;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.dto.response.TestAdminResponse;

public interface TestAdminService {
    PageResponse<TestAdminResponse> getAll(int page, int size, String query, String theme, String tag);

    TestAdminResponse getById(Long id);

    TestAdminResponse create(CreateTestRequest request, String authorUsername);

    TestAdminResponse update(Long id, UpdateTestRequest request);

    TestAdminResponse setQuestions(Long id, SetTestQuestionsRequest request);

    void delete(Long id);
}
