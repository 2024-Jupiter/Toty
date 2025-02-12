package com.toty.post.domain.strategy.update;

import com.toty.common.exception.ErrorCode;
import com.toty.common.exception.ExpectedException;
import com.toty.post.domain.model.Post;
import com.toty.post.domain.model.PostCategory;
import com.toty.post.dto.request.PostUpdateRequest;
import com.toty.user.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KnowledgePostUpdateStrategy implements PostUpdateStrategy {
    @Override
    public Post updatePostRequest(PostUpdateRequest postUpdateRequest, Post post) {
        // 사용자 권한 확인
        if (post.getUser().getRole().equals(Role.USER)) {
            throw new ExpectedException(ErrorCode.USER_NOT_MENTOR);
        }

        return post.updatePost(postUpdateRequest.getTitle(), postUpdateRequest.getContent(), null);
    }

    @Override
    public PostCategory getPostCategory() {
        return PostCategory.KNOWLEDGE;
    }
}
