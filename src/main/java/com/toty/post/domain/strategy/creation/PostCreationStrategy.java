package com.toty.post.domain.strategy.creation;

import com.toty.post.domain.model.Post;
import com.toty.post.domain.model.PostCategory;
import com.toty.post.dto.request.PostCreateRequest;
import com.toty.user.domain.model.User;

public interface PostCreationStrategy {
    Post createPostRequest(PostCreateRequest postCreateRequest, User user);

    PostCategory getPostCategory();
}
