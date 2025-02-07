package com.toty.notification.application.sender;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.toty.base.exception.*;
import com.toty.springconfig.redis.RedisPublisher;
import com.toty.notification.domain.model.Notification;
import com.toty.notification.dto.request.NotificationSendRequest;
import com.toty.springconfig.email.EmailNotificationSender;
import com.toty.springconfig.fcm.FcmNotificationSender;
import com.toty.springconfig.sms.SmsNotificationSender;
import com.toty.springconfig.sse.SseNotificationSender;
import com.toty.user.domain.model.User;
import com.toty.user.domain.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationSenderService {
//    private final Map<String, NotificationSender> senderMap;
    private final UserRepository userRepository;
    private final RedisPublisher redisPublisher;

    public NotificationSenderService(UserRepository userRepository, RedisPublisher redisPublisher) {
        this.userRepository = userRepository;
        this.redisPublisher = redisPublisher;
//        this.senderMap = senders.stream().collect(Collectors.toMap(
//                sender -> getNotificationType(sender.getClass()), sender -> sender
//        ));
    }

    public void send(Notification notification) throws FirebaseMessagingException, MessagingException {
        String type = notification.getType();
        Long userId = notification.getReceiverId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        // 사용자가 알림을 구독한 경우에만 알림을 전송
        if (!user.getSubscribeInfo().isNotificationAllowed()) {
            throw new NotificationDisabledException();
        }

//        NotificationSender sender = senderMap.get(type);
//        if (sender != null) {
//            sender.send(notification);
//        } else {
//            throw new UnsupportedNotificationTypeException(type);
//        }

        // Redis Pub/Sub을 통해 다른 서버로 알림 전송
        NotificationSendRequest sendRequest = convertToSendRequest(notification);
        redisPublisher.publish(sendRequest);
    }

    public NotificationSendRequest convertToSendRequest(Notification notification) {
        return new NotificationSendRequest(
                notification.getReceiverId(),
                notification.getSenderId(),
                notification.getSenderNickname(),
                notification.getType(),
                notification.getUrl()
        );
    }

    // 전략 패턴에 따라 다른 알림 전송
    private String getNotificationType(Class<?> clazz) {
        if (clazz == SseNotificationSender.class) return "Follow, Comment, Like";
        if (clazz == FcmNotificationSender.class) return "ChatRoom, GroupChatRoom, Knowledge, GroupKnowledge, Q/A";
        if (clazz == EmailNotificationSender.class) return "Mento";
        if (clazz == SmsNotificationSender.class) return "Mento";
        return null;
    }
}
