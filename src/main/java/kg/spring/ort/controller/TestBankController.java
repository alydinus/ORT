package kg.spring.ort.controller;

import kg.spring.ort.dto.request.CreateQuestionRequest;
import kg.spring.ort.dto.request.UpdateQuestionRequest;
import kg.spring.ort.dto.response.QuestionResponse;
import kg.spring.ort.mapper.TestBankMapper;
import kg.spring.ort.service.TestBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/test-bank")
public class TestBankController {

    private final TestBankService testBankService;
    private final TestBankMapper testBankMapper;

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getTestBank() {
        return new ResponseEntity<>(testBankService.getAllQuestions().stream()
                .map(testBankMapper::toQuestionResponse)
                .toList(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable Long id) {
        return new ResponseEntity<>(testBankMapper.toQuestionResponse(
                testBankService.getQuestionById(id)), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(CreateQuestionRequest questionRequest) {
        return new ResponseEntity<>(testBankMapper.toQuestionResponse(
                testBankService.createQuestion(
                        questionRequest
                )
        ), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long id, UpdateQuestionRequest questionRequest) {
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
