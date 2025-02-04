package com.toty.chatting.application;

import com.toty.chatting.domain.model.ChatParticipant;
import com.toty.chatting.domain.model.ChatRoom;
import com.toty.chatting.domain.model.User01;
import com.toty.chatting.domain.repository.ChatParticipantRepository;
import com.toty.chatting.domain.repository.ChatRoomRepository;
import com.toty.chatting.domain.repository.User01Repository;
import com.toty.chatting.dto.Participant02DTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomService {

    @Autowired private User01Repository user01Repository;
    @Autowired private ChatRoomRepository chatRoom02Repository;
    @Autowired private ChatParticipantRepository chatParticipant02Repository;

    @Autowired private SimpMessagingTemplate messagingTemplate;


    /*
        개설자가 채팅방 종료
     */
    public void mentorEndRoom(long roomId) {
        ChatRoom chatRoom = chatRoom02Repository.findById(roomId).orElse(null);
        LocalDateTime endTime = LocalDateTime.now();

        // 채팅방 종료시간 입력
        chatRoom.setEndedAt(endTime);
        chatRoom02Repository.save(chatRoom);

        // 채팅방 참석자들 나가는 시간 입력
        List<ChatParticipant> chatterList = chatParticipant02Repository.findAllByRoomAndExitAt(chatRoom, null);
        for (ChatParticipant chatter : chatterList) {
            chatter.setExitAt(endTime);
        }
        chatParticipant02Repository.saveAll(chatterList);

        // 해당 방 종료 알리기( 채팅방에 연결된 웹소켓 통신 종료시키기 )
        String destination = "/chatRoom/" + roomId + "/door";
        messagingTemplate.convertAndSend(destination, "DISCONNECT");
    }

    /*
        채팅방 참석자가, 해당방 나가는 기능
     */
    public void chatterExitRoom(long roomId, long chatterId) {
        ChatRoom chatRoom = chatRoom02Repository.findById(roomId).orElse(null);
        User01 participant = user01Repository.findById(chatterId).orElse(null);

        if (participant != null && chatRoom != null) {
            Optional<ChatParticipant> attendance = chatParticipant02Repository.findByChatterAndRoomAndExitAt(participant, chatRoom, null);
            if (!attendance.isEmpty()) {
                ChatParticipant chatParticipant02 = attendance.get();
                chatParticipant02.setExitAt(LocalDateTime.now()); // 여기까지하면 update될줄 알았는데, 안됨
                chatParticipant02Repository.save(chatParticipant02);

                long chatter01Id = chatParticipant02.getId();
                String chatterName = chatParticipant02.getChatter().getUserName();

                Participant02DTO participantDTO = Participant02DTO.builder()
                        .chatterId(chatter01Id).chatterName(chatterName).access(0)
                        .build();

                // 해당 방에 퇴장 알리기
                String destination = "/chatRoom/" + roomId + "/door";
                messagingTemplate.convertAndSend(destination, participantDTO);
            }
        }
    }
    
    /*
        사용자가 단톡방 입장하기
     */
    public void userEnterRoom(long rid, long uid) {
        // 일단은 단톡방으로 무조건 넘어가는 로직만 있음
        // 여러가지 경우를 고려해야할수도( 단톡방 없는경우 등 )
        ChatRoom chatRoom = chatRoom02Repository.findById(rid).orElse(null);
        User01 participant = user01Repository.findById(uid).orElse(null);
        // 로그인 여부까지 체크하면 좋음

        Optional<ChatParticipant> attendance = chatParticipant02Repository.findByChatterAndRoomAndExitAt(participant, chatRoom, null);
        if (attendance.isEmpty()) {
            if (participant != null && chatRoom != null) {
                ChatParticipant chatter = ChatParticipant.builder()
                        .room(chatRoom).chatter(participant)
                        .build();
                ChatParticipant savedChatter = chatParticipant02Repository.save(chatter);

                long chatterId = savedChatter.getId();
                String chatterName = savedChatter.getChatter().getUserName();

                Participant02DTO participantDTO = Participant02DTO.builder()
                        .chatterId(chatterId).chatterName(chatterName).access(1)
                        .build();

                // 해당 방에 입장 알리기
                messagingTemplate.convertAndSend(
                        "/chatRoom/" + rid + "/door", participantDTO);

            } else {
                // throw new Exception();
            }
        }
    }

    /*
        아직 종료안된 단체톡방들 select
     */
    public List<ChatRoom> getChatRoomList() {
        List<ChatRoom> roomList = chatRoom02Repository.findAllByEndedAt(null);
        return roomList;
    }


    /*
        멘토가 단톡방 생성
     */
    public void mentorCreateRoom(long mid, String roomName, int userLimit) {

        User01 mentor = user01Repository.findById(mid).orElse(null);

        if (mentor != null && mentor.getRole().name() == "MENTOR") {
            ChatRoom room = ChatRoom.builder()
                    .mentor(mentor).roomName(roomName).userLimit(userLimit)
                    .build();
            chatRoom02Repository.save(room);
        } else {
            // throw new Exception();
        }
    }


}
