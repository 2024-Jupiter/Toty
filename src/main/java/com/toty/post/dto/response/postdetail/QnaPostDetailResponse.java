package com.toty.post.dto.response.postdetail;

import com.toty.common.pagination.PaginationResult;
import com.toty.post.domain.model.PostCategory;
import com.toty.post.domain.model.PostImage;
import com.toty.post.domain.model.PostTag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaPostDetailResponse extends PostDetailResponse {
    private List<PostTag> postTags;

    public QnaPostDetailResponse(String nickname, String profileImageUrl, PostCategory postCategory, String title, String content,
                                 List<PostImage> postImages, int viewCount, int likeCount, List<PostTag> postTags, LocalDateTime earliestTime, PaginationResult comments) {
        super(nickname, profileImageUrl, postCategory, title, content, postImages, viewCount, likeCount, earliestTime, comments);
        this.postTags = postTags;
    }
}
