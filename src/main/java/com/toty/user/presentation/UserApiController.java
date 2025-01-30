package com.toty.user.presentation;

import com.toty.annotation.CurrentUser;
import com.toty.following.application.FollowService;
import com.toty.following.dto.response.FollowingListResponse;
import com.toty.user.application.UserService;
import com.toty.user.application.UserSignUpService;
import com.toty.user.domain.model.User;
import com.toty.user.dto.request.UserInfoUpdateRequest;
import com.toty.user.dto.request.UserSignUpRequest;
import com.toty.user.dto.response.UserInfoResponse;
import jakarta.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// 컨트롤러 요소 빠진 것 없는지,
//
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final UserSignUpService userSignUpService;
    private final FollowService followService;

    // 회원 가입
    @PostMapping("/")
    public ResponseEntity<Long> signUp(@RequestBody UserSignUpRequest userSignUpRequest) {
        Long userId = userSignUpService.signUp(userSignUpRequest);
        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }

    // 회원가입 - 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<String> emailValidation(@RequestParam(name = "email") String email) {
        String response = userSignUpService.validateEmail(email);
        return ResponseEntity.ok(response);
    }

    // 회원가입 - 닉네임 증복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<String> nicknameValidation(@RequestParam(name = "nickname") String nickname) {
        String response = userSignUpService.validateNickname(nickname);
        return ResponseEntity.ok(response);
    }

    // 나의/상대방의 정보 보기
        // 본인인지 아닌지 확인 -> 아니면 약식 정보만 전달
    @GetMapping("/{id}")
    public ResponseEntity<UserInfoResponse> getUserInfo(@CurrentUser User user,
                                                        @PathVariable("id") Long id) {
        UserInfoResponse userInfo = userService.getUserInfo(user, id);
        return ResponseEntity.ok(userInfo);
    }

    // 내 정보 수정
    @PatchMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
        public ResponseEntity<String> updateUserInfo(@CurrentUser User user,
                                                     @RequestPart UserInfoUpdateRequest newInfo,
                                                     @RequestPart MultipartFile imgFile) {
        userService.updateUserInfo(user.getId(), newInfo, imgFile);
        return ResponseEntity.ok("Done");
    }
}