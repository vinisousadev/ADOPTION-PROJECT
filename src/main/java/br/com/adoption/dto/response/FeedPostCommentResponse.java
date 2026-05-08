package br.com.adoption.dto.response;

import java.time.OffsetDateTime;

public class FeedPostCommentResponse {

    private Long id;
    private Long feedPostId;
    private Long authorUserId;
    private String authorName;
    private String authorProfilePhotoUrl;
    private String authorRoleLabel;
    private String content;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

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

    public Long getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(Long authorUserId) {
        this.authorUserId = authorUserId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorProfilePhotoUrl() {
        return authorProfilePhotoUrl;
    }

    public void setAuthorProfilePhotoUrl(String authorProfilePhotoUrl) {
        this.authorProfilePhotoUrl = authorProfilePhotoUrl;
    }

    public String getAuthorRoleLabel() {
        return authorRoleLabel;
    }

    public void setAuthorRoleLabel(String authorRoleLabel) {
        this.authorRoleLabel = authorRoleLabel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
