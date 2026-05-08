package br.com.adoption.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Feed post response payload")
public class FeedPostResponse {

    private Long id;
    private Long authorUserId;
    private String authorName;
    private String authorProfilePhotoUrl;
    private String authorRoleLabel;
    private String content;
    private String imageUrl;
    private String videoUrl;
    private Long animalId;
    private String animalName;
    private OffsetDateTime createdAt;
    private long likeCount;
    private long commentCount;
    private boolean likedByCurrentUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }
}
