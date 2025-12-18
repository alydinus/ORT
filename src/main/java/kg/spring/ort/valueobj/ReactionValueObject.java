package kg.spring.ort.valueobj;

import lombok.Getter;

@Getter
public enum ReactionValueObject {
    LIKE("LIKE"),
    DISLIKE("DISLIKE");
    private final String value;

    ReactionValueObject(String value) {
        this.value = value;
    }
}
