package br.com.adoption.mapper;

import br.com.adoption.dto.response.FeedPostVideoResponse;
import br.com.adoption.entity.FeedPost;
import br.com.adoption.entity.FeedPostVideo;

public class FeedPostVideoMapper {

    public static FeedPostVideoResponse toResponse(FeedPostVideo video) {
        FeedPostVideoResponse response = new FeedPostVideoResponse();
        FeedPost feedPost = video.getFeedPost();

        response.setId(video.getId());
        response.setFeedPostId(feedPost != null ? feedPost.getId() : null);
        response.setVideoUrl(video.getVideoUrl());
        response.setCreatedAt(video.getCreatedAt());

        return response;
    }
}
