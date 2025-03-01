package io.chat.domain.chat.controller;

import io.chat.domain.chat.dto.ChatRoomResponseDto;
import io.chat.domain.chat.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // Group Chat
    @PostMapping("/room/group/create")
    public ResponseEntity<ChatRoomResponseDto> createGroupRoom(@RequestParam String roomName){

        ChatRoomResponseDto chatRoomResponseDto = chatService.createGroupRoom(roomName);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(chatRoomResponseDto);

    }
}
