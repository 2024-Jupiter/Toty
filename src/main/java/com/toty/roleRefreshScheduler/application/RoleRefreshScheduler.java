package com.toty.roleRefreshScheduler.application;

import com.toty.following.application.FollowService;

import com.toty.notification.application.service.NotificationSendService;
import com.toty.notification.dto.request.NotificationSendRequest;
import com.toty.roleRefreshScheduler.dto.UserIdAndRoleDto;
import com.toty.user.application.UserService;
import com.toty.user.domain.model.Role;
import com.toty.user.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleRefreshScheduler {

    private final UserRepository userRepository;
    private final UserService userService;
    private final FollowService followService;
    private static final long MINIMUM_MENTOR_FOLLOWERS = 100;

    //멘토 선정
    @Scheduled(cron = "0 0 0 1 * ?") // 매월 1일 자정 수행(초 분 시간 일 월 요일)
    public void refreshRole() {
        List<UserIdAndRoleDto> users = userRepository.findAllByIsDeletedFalse();

        users.forEach(user -> {
            Role desiredRole = isQualified(user.getId()) ? Role.MENTOR : Role.USER;
            if (user.getRole() != desiredRole) {
                userService.updateUserRole(new UserIdAndRoleDto(user.getId(), desiredRole));
            }
        });
    }

    private boolean isQualified(Long id) {
        return followService.countFollowers(id) >= MINIMUM_MENTOR_FOLLOWERS;
    }
}
