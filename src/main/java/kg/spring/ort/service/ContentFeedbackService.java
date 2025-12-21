package kg.spring.ort.service;

import kg.spring.ort.entity.ContentTargetType;
import kg.spring.ort.dto.response.ReactionsSummaryResponse;
import kg.spring.ort.dto.response.PageResponse;
import kg.spring.ort.dto.response.ContentCommentResponse;
import kg.spring.ort.dto.request.SetReactionRequest;
import kg.spring.ort.dto.request.CreateContentCommentRequest;

public interface ContentFeedbackService {
    ContentCommentResponse createComment(ContentTargetType targetType, Long targetId, String username, CreateContentCommentRequest request);

    PageResponse<ContentCommentResponse> getComments(ContentTargetType targetType, Long targetId, int page, int size);

    ReactionsSummaryResponse setReaction(ContentTargetType targetType, Long targetId, String username, SetReactionRequest request);

    ReactionsSummaryResponse getReactions(ContentTargetType targetType, Long targetId, String usernameOrNull);

}


