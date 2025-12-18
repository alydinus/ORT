package kg.spring.ort.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String string) {
        super(string);
    }
}
