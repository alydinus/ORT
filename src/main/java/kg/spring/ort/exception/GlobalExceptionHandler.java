package kg.spring.ort.exception;

import kg.spring.ort.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException ex) {
        return handler(ex.getErrorCode(), ex.getStatus().value(), ex.getMessage());
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleArticleNotFoundException(ArticleNotFoundException ex) {
        return handler("ARTICLE_NOT_FOUND", HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return handler("NOT_FOUND", HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + (err.getDefaultMessage() == null ? "некорректное значение" : err.getDefaultMessage()))
                .orElse("Некорректные данные");
        return handler("VALIDATION_ERROR", HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName() == null ? "параметр" : ex.getName();
        return handler("BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), "Некорректный " + name);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(Exception ex) {
        return handler("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Внутренняя ошибка сервера");
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
