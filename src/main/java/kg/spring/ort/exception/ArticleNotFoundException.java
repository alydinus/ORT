package kg.spring.ort.exception;

public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException(String articleNotFound) {
        super(articleNotFound);
    }
}
