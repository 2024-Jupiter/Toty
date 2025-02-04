package com.toty.chatting.presentation;


import com.toty.chatting.domain.model.ChatParticipant;
import com.toty.chatting.domain.model.ChatRoom;
import com.toty.chatting.domain.model.Role;
import com.toty.chatting.domain.model.User01;
import com.toty.chatting.domain.repository.ChatParticipantRepository;
import com.toty.chatting.domain.repository.ChatRoomRepository;
import com.toty.chatting.domain.repository.User01Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/view/chatting")
public class ChattingViewController {

    @Autowired
    private User01Repository user01Repository;
    @Autowired private ChatParticipantRepository chatParticipant02Repository;
    @Autowired private ChatRoomRepository chatRoom02Repository;

    /*
        유저, 단톡방 목록 화면
     */
    @RequestMapping("/list")
    public String chatList(Model model) {
        List<User01> userList = user01Repository.findAll();
        model.addAttribute("userList", userList);
        return "chatting/chatList";
    }

    /*
        단통방 화면
     */
    @RequestMapping("/room")
    public String aachr(@RequestParam("rid") long rid, Model model) {
        Optional<ChatRoom> room = chatRoom02Repository.findById(rid);

        if (!room.isEmpty()) {
            List<ChatParticipant> chatterList = chatParticipant02Repository.findAllByRoomAndExitAt(room.get(), null);
            model.addAttribute("chatterList", chatterList);
            model.addAttribute("room", room.get());
        }

        return "chatting/chatRoom";
    }



    /*
        프론트에서 form태그 쓰면 브라우저 url이 바뀌어서 redirect해줘야함
        회원가입(user)
     */
    @PostMapping("/user")
    public String aa12 (@RequestParam("userName") String userName) {
        Role userRole = Role.USER;

        User01 uu = User01.builder()
                .userName(userName).role(userRole)
                .build();

        user01Repository.save(uu);
        return "redirect:/view/chatting/list";
    }

    /*
        회원가입(mentor)
     */
    @PostMapping("/mentor")
    public String aa (@RequestParam("userName") String userName) {
        Role userRole = Role.MENTOR;

        User01 uu = User01.builder()
                .userName(userName).role(userRole)
                .build();

        user01Repository.save(uu);
        return "redirect:/view/chatting/list";
    }

}
