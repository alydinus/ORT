package kg.spring.ort.controller;

import jakarta.validation.Valid;
import kg.spring.ort.dto.request.CreateQuestionRequest;
import kg.spring.ort.dto.request.UpdateQuestionRequest;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.dto.response.QuestionResponse;
import kg.spring.ort.mapper.TestBankMapper;
import kg.spring.ort.service.TestBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/test-bank")
@PreAuthorize("hasAnyAuthority('ROLE_MODERATOR','ROLE_ADMIN')")
public class TestBankController {

    private final TestBankService testBankService;
    private final TestBankMapper testBankMapper;

    @GetMapping
    public ResponseEntity<PageResponse<QuestionResponse>> getTestBank(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String query
    ) {
        var result = testBankService.getQuestionsPage(page, size, query);
        return new ResponseEntity<>(
                new PageResponse<>(
                        result.content().stream().map(testBankMapper::toQuestionResponse).toList(),
                        result.page(),
                        result.size(),
                        result.totalElements(),
                        result.totalPages(),
                        result.last()
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable Long id) {
        return new ResponseEntity<>(testBankMapper.toQuestionResponse(
                testBankService.getQuestionById(id)), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody CreateQuestionRequest questionRequest) {
        return new ResponseEntity<>(testBankMapper.toQuestionResponse(
                testBankService.createQuestion(
                        questionRequest
                )
        ), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateQuestionRequest questionRequest) {
        return new ResponseEntity<>(testBankMapper.toQuestionResponse(
                testBankService.updateQuestion(
                        id,
                        questionRequest
                )), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        testBankService.deleteQuestion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
