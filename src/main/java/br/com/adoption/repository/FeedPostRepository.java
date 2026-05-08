package br.com.adoption.repository;

import br.com.adoption.entity.FeedPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedPostRepository extends JpaRepository<FeedPost, Long> {
}
