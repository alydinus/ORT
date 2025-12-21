package kg.spring.ort.dto.request;
import kg.spring.ort.valueobj.ReactionValueObject;
import jakarta.validation.constraints.NotNull;
public record SetReactionRequest(@NotNull ReactionValueObject value) {
}

