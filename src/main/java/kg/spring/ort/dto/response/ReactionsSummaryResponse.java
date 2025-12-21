package kg.spring.ort.dto.response;


import kg.spring.ort.valueobj.ReactionValueObject;

public record ReactionsSummaryResponse(
        long likes,
        long dislikes,
        ReactionValueObject myReaction
) {
}

