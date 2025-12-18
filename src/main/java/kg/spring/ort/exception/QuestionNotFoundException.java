package kg.spring.ort.exception;

public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException(String s) {
        super(s);
    }
}
