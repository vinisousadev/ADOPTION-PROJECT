package br.com.adoption.mapper;

import br.com.adoption.dto.response.FeedPostCommentResponse;
import br.com.adoption.entity.FeedPost;
import br.com.adoption.entity.FeedPostComment;
import br.com.adoption.entity.User;

public class FeedPostCommentMapper {

    public static FeedPostCommentResponse toResponse(FeedPostComment comment) {
        FeedPostCommentResponse response = new FeedPostCommentResponse();
        FeedPost feedPost = comment.getFeedPost();
        User author = comment.getAuthor();

        response.setId(comment.getId());
        response.setFeedPostId(feedPost != null ? feedPost.getId() : null);
        response.setAuthorUserId(author != null ? author.getId() : null);
        response.setAuthorName(author != null ? author.getName() : null);
        response.setAuthorProfilePhotoUrl(author != null ? author.getProfilePhotoUrl() : null);
        response.setAuthorRoleLabel(author != null && author.getRoleLabel() != null ? author.getRoleLabel().name() : null);
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());

        return response;
    }
}
