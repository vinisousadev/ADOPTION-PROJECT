package br.com.adoption.repository;

import br.com.adoption.entity.FeedPostVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedPostVideoRepository extends JpaRepository<FeedPostVideo, Long> {
    Optional<FeedPostVideo> findByFeedPost_Id(Long feedPostId);
}
