package com.toty.post.domain.strategy.update;

import com.toty.common.exception.ErrorCode;
import com.toty.common.exception.ExpectedException;
import com.toty.post.application.PostImageService;
import com.toty.post.domain.model.Post;
import com.toty.post.domain.model.PostCategory;
import com.toty.post.domain.model.PostTag;
import com.toty.post.domain.repository.PostRepository;
import com.toty.post.domain.repository.PostTagRepository;
import com.toty.post.dto.request.PostUpdateRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QnaPostUpdateStrategy implements PostUpdateStrategy {
    private final PostImageService postImageService;
    private final PostTagRepository postTagRepository;
    private final PostRepository postRepository;

    @Override
    public Post updatePostRequest(PostUpdateRequest postUpdateRequest, Post post) {
        // 태그 검증
        List<PostTag> postTags = postUpdateRequest.getPostTags();
        Optional.ofNullable(postTags)
                .filter(tags -> !tags.isEmpty())
                .orElseThrow(() -> new ExpectedException(ErrorCode.MISSING_REQUIRED_TAG));
        if (postTags.size() > 5) {
            throw new ExpectedException(ErrorCode.TAG_LIMIT_EXCEEDED);
        }

        Post updatedPost = new Post(post.getUser(), post.getPostCategory(), postUpdateRequest.getTitle(), postUpdateRequest.getContent(),
                post.getViewCount(), post.getLikeCount(), post.getComments(), null, postUpdateRequest.getPostTags());

        // 태그
        for (PostTag postTag: postTags) {
            postTagRepository.save(postTag);
        }

        // 이미지
        synchronizeImages(updatedPost, postUpdateRequest.getPostImages(), postImageService);

        postRepository.save(updatedPost);
        return updatedPost;
    }

    @Override
    public PostCategory getPostCategory() {
        return PostCategory.QnA;
    }
}
