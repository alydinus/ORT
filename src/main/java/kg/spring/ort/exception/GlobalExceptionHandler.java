package kg.spring.ort.exception;

import kg.spring.ort.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleArticleNotFoundException(ArticleNotFoundException ex) {
        return handler("ARTICLE_NOT_FOUND", HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        return handler("BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> handler(String errorCode, int status, String message) {
        ErrorResponse error = new ErrorResponse(
                errorCode,
                message,
                status,
                System.currentTimeMillis()
        );
        return ResponseEntity.status(status).body(error);
    }
}
