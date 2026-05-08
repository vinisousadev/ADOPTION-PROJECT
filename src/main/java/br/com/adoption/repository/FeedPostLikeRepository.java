package br.com.adoption.repository;

import br.com.adoption.entity.FeedPostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedPostLikeRepository extends JpaRepository<FeedPostLike, Long> {
    long countByFeedPost_Id(Long feedPostId);
    boolean existsByFeedPost_IdAndUser_Id(Long feedPostId, Long userId);
    Optional<FeedPostLike> findByFeedPost_IdAndUser_Id(Long feedPostId, Long userId);
}
