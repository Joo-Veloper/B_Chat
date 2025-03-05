package io.chat.domain.chat.controller;

import io.chat.domain.chat.dto.*;
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

    // Look up previous messages
    @GetMapping("/history/{roomId}")
    public ResponseEntity<List<ChatMessageResponseDto>> getChatHistory(@PathVariable Long roomId) {

        List<ChatMessageResponseDto> chatMessageResponseDto = chatService.getChatHistory(roomId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(chatMessageResponseDto);
    }

    // Processing Chat Message Read
    @PatchMapping("/room/{roomId}/read")
    public ResponseEntity<ChatReadResponseDto> messageRead(@PathVariable Long roomId) {

        chatService.messageRead(roomId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ChatReadResponseDto("Messages marked as read successfully"));
    }


    // Look up my chatroom list
    @GetMapping("/my/rooms")
    public ResponseEntity<List<MyChatListResponseDto>> getMyChatRooms() {

        List<MyChatListResponseDto> myChatListResponseDtos = chatService.getMyChatRoom();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(myChatListResponseDtos);
    }

    // leave the chat room
    @DeleteMapping("/room/group/{roomId}/leave")
    public ResponseEntity<Void> leaveGroupChatRoom(@PathVariable Long roomId) {

        chatService.leaveChatRoom(roomId);

        return ResponseEntity.ok().build();
    }

    // 개인 채팅방 개설 또는 기존 roomId return
    @PostMapping("/room/private/create")
    public ResponseEntity<?> getOrCreatePrivateRoom(@RequestParam Long otherMemberId) {
        Long roomId = chatService.getOrCreatePrivateRoom(otherMemberId);
        return new ResponseEntity<>(roomId, HttpStatus.OK);
    }
}
