package com.toty.post.application;

import com.toty.base.exception.UnauthorizedException;
import com.toty.base.exception.UserNotFoundException;
import com.toty.base.exception.PostNotFoundException;
import com.toty.base.pagination.PaginationResult;
import com.toty.post.application.strategy.convert.postdetail.PostDetailResponseContext;
import com.toty.post.application.strategy.convert.postlist.PostListResponseContext;
import com.toty.post.application.strategy.creation.PostCreationStrategy;
import com.toty.post.application.strategy.update.PostUpdateStrategy;
import com.toty.post.domain.model.Post;
import com.toty.post.domain.pagination.PostPaginationStrategy;
import com.toty.post.domain.repository.PostRepository;
import com.toty.post.domain.repository.PostSpecifications;
import com.toty.post.presentation.dto.request.PostCreateRequest;
import com.toty.post.presentation.dto.request.PostUpdateRequest;
import com.toty.post.presentation.dto.response.postdetail.PostDetailResponse;
import com.toty.post.presentation.dto.response.postlist.GeneralPostListResponse;
import com.toty.post.presentation.dto.response.postlist.PostListResponse;
import com.toty.user.domain.User;
import com.toty.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostCreationStrategy postCreationStrategy;
    private final PostUpdateStrategy postUpdateStrategy;
    private final PostPaginationStrategy postPaginationStrategy;

    private static final int PAGE_SIZE = 10;  // 기본 페이지 수

    // 게시글 가져 오기
    public Post findPostById(Long id) {
        return postRepository.findById(id).orElseThrow(PostNotFoundException::new);
    }

    // 게시글 작성
    public Post createPost(Long userId, PostCreateRequest postCreateRequest) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        return postCreationStrategy.createPostRequest(postCreateRequest, user);
    }

    // 게시글 수정
    public Post updatePost(Long id, PostUpdateRequest postUpdateRequest) {
        // 내 게시글 인지 확인 필요
//        if (!isOwner(id)) {
//            throw new UnauthorizedException();
//        }

        Post post = findPostById(id);
        return postUpdateStrategy.updatePostRequest(postUpdateRequest, post);
    }

    // 게시글 삭제
    public void deletePost(Long id) {
        Post post = findPostById(id);
        // 내 게시글 인지 확인 필요
//        if (!isOwner(id)) {
//            throw new UnauthorizedException();
//        }

        // 정말로 삭제 할 것인지 확인 필요 - 프론트에서 처리
        postRepository.delete(post);
    }

    // 전체 게시글 목록 조회 (수정시간 기준으로 최신순 정렬 - 오늘 / 이번 주 / 이번 딜)
    public PaginationResult getPagedPosts(int page, String filter) {
        Specification<Post> specification = PostSpecifications.isNotDeleted();
        if ("today".equals(filter)) {
            specification = specification.and(PostSpecifications.isToday());
        } else if ("thisWeek".equals(filter)) {
            specification = specification.and(PostSpecifications.isThisWeek());
        } else if ("thisMonth".equals(filter)) {
            specification = specification.and(PostSpecifications.isThisMonth());
        }

        PageRequest pageRequest = PageRequest.of(page - 1, PAGE_SIZE, Sort.by(Sort.Order.desc("updatedAt")));
        Page<Post> posts = postRepository.findAll(specification, pageRequest);

        // Post -> PostListResponse 변환
        List<GeneralPostListResponse> postLists = posts.getContent().stream()
                .map(this::toGeneralPostListResponse)
                .toList();

        return postPaginationStrategy.getPaginationResult(posts, PAGE_SIZE, postLists);
    }

    // 사용자 ID로 필터링된 게시글 목록 조회
    public PaginationResult getPagedPostsByUserId(int page, Long userId, String postCategory) {
        PageRequest pageRequest = PageRequest.of(page - 1, PAGE_SIZE, Sort.by(Sort.Order.asc("updatedAt")));
        Page<Post> posts = postRepository.findAll(
                Specification.where(PostSpecifications.isNotDeleted())
                        .and(PostSpecifications.hasUserId(userId))
                        .and(PostSpecifications.hasCategory(postCategory)),
                pageRequest);

        PostListResponseContext context = new PostListResponseContext(postCategory);
        List<? extends PostListResponse> postLists = context.convertPosts(posts.getContent());

        return postPaginationStrategy.getPaginationResult(posts, PAGE_SIZE, postLists);
    }

    // 카테고리로 필터링된 게시글 목록 조회
    public PaginationResult getPagedPostsByCategory(int page, String postCategory) {
        PageRequest pageRequest = PageRequest.of(page - 1, PAGE_SIZE, Sort.by(Sort.Order.asc("updatedAt")));
        Page<Post> posts = postRepository.findAll(
                Specification.where(PostSpecifications.isNotDeleted())
                        .and(PostSpecifications.hasCategory(postCategory)),
                pageRequest);

        PostListResponseContext context = new PostListResponseContext(postCategory);
        List<? extends PostListResponse> postLists = context.convertPosts(posts.getContent());

        return postPaginationStrategy.getPaginationResult(posts, PAGE_SIZE, postLists);
    }

    // 카테고리 별 게시글 상세 보기
    public PostDetailResponse getPostDetailByCategory(Long id, String postCategory) {
        Post post = findPostById(id);
        // 댓글 필요
        PostDetailResponseContext context = new PostDetailResponseContext(postCategory);
        return context.convertPost(post);
    }

    // 조회수 증가 (동시성 고려)
    public void incrementViewCount(Long id) {
        postRepository.updateViewCount(id);
    }

    // 좋아요 증가 (동시성 고려)
    public void incrementLikeCount(Long id) {
        postRepository.updateLikeCount(id, 1);
    }

    // 좋아요 감소 (동시성 고려)
    public void decrementLikeCount(Long id) {
        postRepository.updateLikeCount(id, -1);
    }

    // Post -> PostListResponse
    private GeneralPostListResponse toGeneralPostListResponse(Post post) {
        return new GeneralPostListResponse(
                post.getUser().getNickname(),                   // 사용자 닉네임
                post.getUser().getProfileImageUrl(),            // 프로필 이미지 URL
                post.getTitle(),                                // 제목
                post.getViewCount(),                            // 조회수
                post.getLikeCount(),                            // 좋아요 수
                post.getUpdatedAt()                             // 생성 일시과 수정 일시 중 더 나중에 된 시간
        );
    }

//    public boolean isOwner(Long postId) {
//        Long userId = getCurrentUserId(); // 인증된 사용자 ID 가져오기
//        Post post = findPostById(postId);
//        return post.getAuthor().getId().equals(userId); // 게시글 작성자와 비교
//    }
//
//    private Long getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
//            throw new UnauthorizedException("인증 정보가 없습니다.");
//        }
//        return (Long) authentication.getPrincipal();
//    }
}
