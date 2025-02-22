package com.toty.notification.domain.factory.message;

import com.toty.common.baseException.UnSupportedNotificationTypeException;
import com.toty.notification.domain.strategy.message.NotificationMessageStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NotificationMessageFactory {
    private final Map<String, NotificationMessageStrategy> strategies;

    @Autowired
    public NotificationMessageFactory(List<NotificationMessageStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(s -> s.getClass().getSimpleName().replace("MessageStrategy", "").toUpperCase(), s -> s));
    }

    public String generateMessage(String type, String sender) {
        NotificationMessageStrategy strategy = strategies.get(type.toUpperCase());
        if (strategy != null) {
            return strategy.generateMessage(sender);
        }
        throw new UnSupportedNotificationTypeException(type);
    }
}

