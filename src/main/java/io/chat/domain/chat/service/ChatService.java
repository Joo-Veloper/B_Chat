package io.chat.domain.chat.service;


import io.chat.domain.chat.dto.ChatMessageRequestDto;
import io.chat.domain.chat.dto.ChatRoomResponseDto;

public interface ChatService {

    void saveMessage(Long roomId, ChatMessageRequestDto chatMessageRequestDto);

    ChatRoomResponseDto createGroupRoom(String roomName);
}
