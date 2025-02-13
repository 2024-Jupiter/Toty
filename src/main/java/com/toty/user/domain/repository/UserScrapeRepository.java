package com.toty.user.domain.repository;

import com.toty.post.domain.model.Post;
import com.toty.post.domain.model.PostCategory;
import com.toty.user.domain.model.User;
import com.toty.user.domain.model.UserScrape;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserScrapeRepository extends JpaRepository<UserScrape, Long> {
    // 저장된 게시글 찾기
    Optional<UserScrape> findByUserAndPost(User user, Post post);

    boolean existsByUserAndPost(User user, Post post);

    @Query("SELECT p FROM UserScrape us JOIN us.post p WHERE us.user.id = :userId AND p.postCategory = :postCategory")
    Page<Post> findPostsByUserIdAndPostCategory(@Param("userId") Long userId, @Param("postCategory") PostCategory postCategory, Pageable pageable);
}
