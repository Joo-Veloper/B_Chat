package io.chat.domain.chat.controller;

import io.chat.domain.chat.dto.ChatRoomJoinResponseDto;
import io.chat.domain.chat.dto.ChatRoomListResponseDto;
import io.chat.domain.chat.dto.ChatRoomResponseDto;
import io.chat.domain.chat.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // Group Chat
    @PostMapping("/room/group/create")
    public ResponseEntity<ChatRoomResponseDto> createGroupRoom(@RequestParam String roomName) {

        ChatRoomResponseDto chatRoomResponseDto = chatService.createGroupRoom(roomName);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(chatRoomResponseDto);

    }

    // Group chat get
    @GetMapping("/room/group/list")
    public ResponseEntity<List<ChatRoomListResponseDto>> getGroupChatRoom() {

        List<ChatRoomListResponseDto> chatRooms = chatService.getGroupChatRooms();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(chatRooms);
    }

    // Join the chat
    @PostMapping("/room/group/{roomId}/join")
    public ResponseEntity<ChatRoomJoinResponseDto> joinGroupChatRoom(@PathVariable Long roomId) {

        chatService.addParticipantToGroupChat(roomId);

        ChatRoomJoinResponseDto responseDto = new ChatRoomJoinResponseDto(roomId, "Successfully joined the chat room");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }
}
