package com.toty.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateUpdateRequest {
    private Long id;
    @NotBlank(message = "내용을 입력 하세요.")
    @Size(min = 1, max = 100, message = "한 글자 이상 내용을 입력 하세요.")
    private String content;

    public CommentCreateUpdateRequest(Long id, String content) {
        this.id = id;
        this.content = content;
    }
}
