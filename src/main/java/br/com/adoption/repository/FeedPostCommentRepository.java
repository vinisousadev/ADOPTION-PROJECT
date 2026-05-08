package br.com.adoption.repository;

import br.com.adoption.entity.FeedPostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedPostCommentRepository extends JpaRepository<FeedPostComment, Long> {
    long countByFeedPost_Id(Long feedPostId);
    Page<FeedPostComment> findByFeedPost_IdOrderByCreatedAtDesc(Long feedPostId, Pageable pageable);
}
