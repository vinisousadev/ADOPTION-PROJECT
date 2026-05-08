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
import jakarta.persistence.UniqueConstraint;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "FEED_POST_LIKE",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_FEED_POST_LIKE_POST_USER", columnNames = {"FEED_POST_ID", "USER_ID"})
        }
)
@SequenceGenerator(
        name = "feed_post_like_seq",
        sequenceName = "SEQ_FEED_POST_LIKE",
        allocationSize = 1
)
public class FeedPostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feed_post_like_seq")
    @Column(name = "ID_FEED_POST_LIKE")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FEED_POST_ID", nullable = false)
    private FeedPost feedPost;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;

    public Long getId() {
        return id;
    }

    public FeedPost getFeedPost() {
        return feedPost;
    }

    public void setFeedPost(FeedPost feedPost) {
        this.feedPost = feedPost;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
