package io.chat.domain.chat.service;


import io.chat.domain.chat.dto.ChatMessageRequestDto;
import io.chat.domain.chat.dto.ChatMessageResponseDto;
import io.chat.domain.chat.dto.ChatRoomListResponseDto;
import io.chat.domain.chat.dto.ChatRoomResponseDto;

import java.util.List;

public interface ChatService {

    void saveMessage(Long roomId, ChatMessageRequestDto chatMessageRequestDto);

    ChatRoomResponseDto createGroupRoom(String roomName);

    List<ChatRoomListResponseDto> getGroupChatRooms();

    void addParticipantToGroupChat(Long roomId);

    List<ChatMessageResponseDto> getChatHistory(Long roomId);

    Boolean isRoomParticipant(String email, Long roomId);
}
