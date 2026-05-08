package br.com.adoption.mapper;

import br.com.adoption.dto.response.FeedPostResponse;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.FeedPost;
import br.com.adoption.entity.User;

public class FeedPostMapper {

    public static FeedPostResponse toResponse(FeedPost feedPost) {
        FeedPostResponse response = new FeedPostResponse();
        User author = feedPost.getAuthor();
        Animal animal = feedPost.getAnimal();

        response.setId(feedPost.getId());
        response.setAuthorUserId(author != null ? author.getId() : null);
        response.setAuthorName(author != null ? author.getName() : null);
        response.setAuthorProfilePhotoUrl(author != null ? author.getProfilePhotoUrl() : null);
        response.setAuthorRoleLabel(author != null && author.getRoleLabel() != null ? author.getRoleLabel().name() : null);
        response.setContent(feedPost.getContent());
        response.setImageUrl(feedPost.getImageUrl());
        response.setAnimalId(animal != null ? animal.getId() : null);
        response.setAnimalName(animal != null ? animal.getAnimalName() : null);
        response.setCreatedAt(feedPost.getCreatedAt());

        return response;
    }

    public static FeedPostResponse toResponse(FeedPost feedPost,
                                              long likeCount,
                                              long commentCount,
                                              boolean likedByCurrentUser,
                                              String videoUrl) {
        FeedPostResponse response = toResponse(feedPost);
        response.setLikeCount(likeCount);
        response.setCommentCount(commentCount);
        response.setLikedByCurrentUser(likedByCurrentUser);
        response.setVideoUrl(videoUrl);
        return response;
    }
}
