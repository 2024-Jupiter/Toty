package com.toty.chatting.application;

import com.toty.chatting.dto.response.ChatRoomListResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ChatListSseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
    }

    public void sendEventCountUp(long roomId) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("countUp").data(roomId));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    public void sendEventCountDown(long roomId) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("countDown").data(roomId));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    public void sendEventEndRoom(long roomId) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("roomEnd").data(roomId));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    public void sendEventCreationRoom(ChatRoomListResponse chatRoomListResponse) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("roomCreation").data(chatRoomListResponse));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
