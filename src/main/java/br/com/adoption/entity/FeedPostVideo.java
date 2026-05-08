package br.com.adoption.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "FEED_POST_VIDEO")
@SequenceGenerator(
        name = "feed_post_video_seq",
        sequenceName = "SEQ_FEED_POST_VIDEO",
        allocationSize = 1
)
public class FeedPostVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feed_post_video_seq")
    @Column(name = "ID_FEED_POST_VIDEO")
    private Long id;

    @OneToOne
    @JoinColumn(name = "FEED_POST_ID", nullable = false, unique = true)
    private FeedPost feedPost;

    @Column(name = "VIDEO_URL", nullable = false, length = 500)
    private String videoUrl;

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
