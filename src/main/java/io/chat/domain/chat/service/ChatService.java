package io.chat.domain.chat.service;


import io.chat.domain.chat.dto.*;

import java.util.List;

public interface ChatService {

    void saveMessage(Long roomId, ChatMessageRequestDto chatMessageRequestDto);

    ChatRoomResponseDto createGroupRoom(String roomName);

    List<ChatRoomListResponseDto> getGroupChatRooms();

    void addParticipantToGroupChat(Long roomId);

    List<ChatMessageResponseDto> getChatHistory(Long roomId);

    Boolean isRoomParticipant(String email, Long roomId);

    void messageRead(Long roomId);

    List<MyChatListResponseDto> getMyChatRoom();

    void leaveChatRoom(Long roomId);

    Long getOrCreatePrivateRoom(Long otherMemberId);
}
