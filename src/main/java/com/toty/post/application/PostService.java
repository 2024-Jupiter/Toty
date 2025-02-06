package com.toty.post.application;

import com.toty.base.exception.*;
import com.toty.post.domain.factory.creation.PostCreationStrategyFactory;
import com.toty.post.domain.factory.update.PostUpdateStrategyFactory;
import com.toty.post.domain.strategy.creation.PostCreationStrategy;
import com.toty.post.domain.strategy.update.PostUpdateStrategy;
import com.toty.post.domain.model.Post;
import com.toty.post.domain.repository.PostRepository;
import com.toty.post.dto.request.PostCreateRequest;
import com.toty.post.dto.request.PostUpdateRequest;
import com.toty.user.domain.model.User;
import com.toty.user.domain.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostCreationStrategyFactory postCreationStrategyFactory;
    private final PostUpdateStrategyFactory postUpdateStrategyFactory;

    private static final int PAGE_SIZE = 10;  // 기본 페이지 수

    // 게시글 가져 오기
    public Post findPostById(Long id) {
        return postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    }

    // 게시글 작성
    @Transactional
    public Post createPost(Long userId, PostCreateRequest postCreateRequest) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        PostCreationStrategy strategy = postCreationStrategyFactory.getStrategy(postCreateRequest.getPostCategory());
        if (strategy == null) {
            throw new PostCategoryCreationNotSupportedException();
        }

        return strategy.createPostRequest(postCreateRequest, user);
    }

    // 본인 계정 확인
    private boolean isOwner(User user, Long postOwnerId) {
        return user.getId().equals(postOwnerId);
    }

    // 게시글 수정
    @Transactional
    public Post updatePost(User user, Long id, PostUpdateRequest postUpdateRequest) {
        Post post = findPostById(id);
        // 내 게시글 인지 확인 필요
        if (isOwner(user, post.getUser().getId())) {
            throw new UnauthorizedException();
        }

        PostUpdateStrategy strategy = postUpdateStrategyFactory.getStrategy(post.getPostCategory());
        if (strategy == null) {
            throw new PostCategoryUpdateNotSupportedException();
        }

        return strategy.updatePostRequest(postUpdateRequest, post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(User user, Long id) {
        Post post = findPostById(id);
        // 내 게시글 인지 확인 필요
        if (isOwner(user, post.getUser().getId())) {
            throw new UnauthorizedException();
        }

        // 정말로 삭제 할 것인지 확인 필요 - 프론트에서 처리
        postRepository.delete(post);
    }

    // 조회수 증가 (동시성 고려)
    @Transactional
    public void incrementViewCount(Long id) {
        postRepository.updateViewCount(id);
    }
}
