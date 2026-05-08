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
@Table(name = "FEED_POST")
@SequenceGenerator(
        name = "feed_post_seq",
        sequenceName = "SEQ_FEED_POST",
        allocationSize = 1
)
public class FeedPost {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feed_post_seq")
    @Column(name = "ID_FEED_POST")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "AUTHOR_USER_ID", nullable = false)
    private User author;

    @Column(name = "CONTENT", nullable = false, length = 1000)
    private String content;

    @Column(name = "IMAGE_URL", length = 500)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "ANIMAL_ID")
    private Animal animal;

    @Column(name = "CREATED_AT", nullable = false)
    private OffsetDateTime createdAt;

    public Long getId() {
        return id;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
