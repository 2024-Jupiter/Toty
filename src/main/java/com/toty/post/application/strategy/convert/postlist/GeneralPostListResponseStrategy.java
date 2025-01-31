package com.toty.post.application.strategy.convert.postlist;

import com.toty.post.domain.model.Post;
import com.toty.post.presentation.dto.response.postlist.GeneralPostListResponse;
import com.toty.post.presentation.dto.response.postlist.PostListResponse;

public class GeneralPostListResponseStrategy implements PostListResponseStrategy {
    @Override
    public PostListResponse convert(Post post) {
        return new GeneralPostListResponse(
                post.getUser().getNickname(),
                post.getUser().getProfileImageUrl(),
                post.getTitle(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getUpdatedAt()
        );
    }
}

