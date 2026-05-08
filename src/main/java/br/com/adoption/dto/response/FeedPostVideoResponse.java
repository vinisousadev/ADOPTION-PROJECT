package br.com.adoption.dto.response;

import java.time.OffsetDateTime;

public class FeedPostVideoResponse {

    private Long id;
    private Long feedPostId;
    private String videoUrl;
    private OffsetDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFeedPostId() {
        return feedPostId;
    }

    public void setFeedPostId(Long feedPostId) {
        this.feedPostId = feedPostId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
