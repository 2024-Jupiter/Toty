package com.toty.post.presentation;

import com.toty.common.response.TotyResponse;
import com.toty.post.application.PostSearchService;
import com.toty.post.domain.model.PostCategory;
import com.toty.post.domain.model.elasticsearch.PostEs;
import com.toty.post.domain.model.elasticsearch.SearchField;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search/posts")
public class PostSearchApiController {

    private final PostSearchService postSearchService;

    /**
     * 통합 검색 API
     * 검색 시 처음 나오는 화면에 보여질 모든 게시판(일반, 정보, QnA)의 데이터들을 리턴합니다.
     *
     * @param keyword 검색어
     * @param field 검색 속성 (제목, 본문, 제목 + 본문)
     * @param size 페이지당 게시글 개수 (기본값: 5)
     * @return Map<게시판 종류(일반, 정보, QnA), Page<게시글(본문, 댓글 제외)>>
     */
    @GetMapping
    public TotyResponse<Map<PostCategory, Page<PostEs>>> searchPosts (
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "field") SearchField field,
            @RequestParam(defaultValue = "5") int size
    ) {
        return TotyResponse.success(postSearchService.searchPosts(keyword, field, size));
    }

    /**
     * 단일 검색 API
     * 검색 후 나오는 화면에서 각 게시판의 페이지에 따라 데이터들을 리턴합니다.
     *
     * @param keyword 검색어
     * @param field 검색 속성 (제목, 본문, 제목 + 본문)
     * @param category 게시판 종류 (일반, 정보, QnA)
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지당 게시글 개수 (기본값: 5)
     * @return Map<게시판 종류(일반, 정보, QnA), Page<게시글(본문, 댓글 제외)>>
     */
    @GetMapping("/category")
    public TotyResponse<Map<PostCategory, Page<PostEs>>> searchPostsByCategory (
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "field") SearchField field,
            @RequestParam(name = "category") PostCategory category,
            @RequestParam(name = "page") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return TotyResponse.success(postSearchService.searchPostsByCategory(keyword, field, category, page, size));
    }

    // todo : 추후 게시글 작성 로직에 추가된 후 삭제될 API 입니다.
    @PostMapping("/create")
    public TotyResponse<String> createPost(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "category") PostCategory category) {
        String nickname = "테스트닉네임";
        String postId = postSearchService.savePost(title, content, nickname, category);
        return TotyResponse.success(postId);
    }
}

