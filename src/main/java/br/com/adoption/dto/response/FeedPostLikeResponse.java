package br.com.adoption.dto.response;

public class FeedPostLikeResponse {

    private Long feedPostId;
    private Long userId;
    private boolean liked;
    private long likeCount;

    public Long getFeedPostId() {
        return feedPostId;
    }

    public void setFeedPostId(Long feedPostId) {
        this.feedPostId = feedPostId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }
}
