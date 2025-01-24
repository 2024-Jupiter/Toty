package com.toty.post.application.strategy.convert.postdetail;

import com.toty.post.domain.model.Post;
import com.toty.post.presentation.dto.response.postdetail.GeneralPostDetailResponse;
import com.toty.post.presentation.dto.response.postdetail.PostDetailResponse;

public class GeneralPostDetailResponseStrategy implements PostDetailResponseStrategy {
    @Override
    public PostDetailResponse convert(Post post) {
        return new GeneralPostDetailResponse(
                post.getUser().getNickname(),
                post.getUser().getProfileImageUrl(),
                post.getPostCategory(),
                post.getTitle(),
                post.getContent(),
                post.getPostImages(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getUpdatedAt()
        );
    }
}
