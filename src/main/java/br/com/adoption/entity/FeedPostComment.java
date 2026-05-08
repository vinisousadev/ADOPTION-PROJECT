package br.com.adoption.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "FEED_POST_COMMENT")
@SequenceGenerator(
        name = "feed_post_comment_seq",
        sequenceName = "SEQ_FEED_POST_COMMENT",
        allocationSize = 1
)
public class FeedPostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feed_post_comment_seq")
    @Column(name = "ID_FEED_POST_COMMENT")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FEED_POST_ID", nullable = false)
    private FeedPost feedPost;

    @ManyToOne
    @JoinColumn(name = "AUTHOR_USER_ID", nullable = false)
    private User author;

    @Column(name = "CONTENT", nullable = false, length = 500)
    private String content;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private OffsetDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public FeedPost getFeedPost() {
        return feedPost;
    }

    public void setFeedPost(FeedPost feedPost) {
        this.feedPost = feedPost;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
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
