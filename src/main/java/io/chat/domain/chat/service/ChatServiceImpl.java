package io.chat.domain.chat.service;

import io.chat.domain.chat.dto.ChatMessageRequestDto;
import io.chat.domain.chat.dto.ChatRoomListResponseDto;
import io.chat.domain.chat.dto.ChatRoomResponseDto;
import io.chat.domain.chat.entity.ChatMessage;
import io.chat.domain.chat.entity.ChatParticipant;
import io.chat.domain.chat.entity.ChatRoom;
import io.chat.domain.chat.entity.ReadStatus;
import io.chat.domain.chat.repository.ChatMessageRepository;
import io.chat.domain.chat.repository.ChatParticipantRepository;
import io.chat.domain.chat.repository.ChatRoomRepository;
import io.chat.domain.chat.repository.ReadStatusRepository;
import io.chat.domain.member.entity.Member;
import io.chat.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ChatRoomResponseDto createGroupRoom(String roomName) {

        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("member cannot be found"));

        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .isGroupChat("Y")
                .build();

        chatRoomRepository.save(chatRoom);

        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();

        chatParticipantRepository.save(chatParticipant);

        return new ChatRoomResponseDto(chatRoom.getId(), chatRoom.getName());
    }

    @Override
    public void saveMessage(Long roomId, ChatMessageRequestDto chatMessageRequestDto) {
        ChatRoom chatRoom = getChatRoomById(roomId);
        Member sender = getMemberByEmail(chatMessageRequestDto.getSenderEmail());

        ChatMessage chatMessage = chatMessageRepository.save(
                ChatMessage.builder()
                        .chatRoom(chatRoom)
                        .member(sender)
                        .content(chatMessageRequestDto.getMessage())
                        .build()
        );

        List<ReadStatus> readStatuses = createReadStatuses(chatRoom, chatMessage, sender);
        readStatusRepository.saveAll(readStatuses);
    }

    private ChatRoom getChatRoomById(Long roomId) {

        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));
    }

    private Member getMemberByEmail(String email) {

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }

    private List<ReadStatus> createReadStatuses(ChatRoom chatRoom, ChatMessage chatMessage, Member sender) {

        return chatParticipantRepository.findByChatRoom(chatRoom).stream()
                .map(chatParticipant -> ReadStatus.builder()
                        .chatRoom(chatRoom)
                        .member(chatParticipant.getMember())
                        .chatMessage(chatMessage)
                        .isRead(chatParticipant.getMember().equals(sender))
                        .build())
                .toList();
    }

    @Override
    public List<ChatRoomListResponseDto> getGroupChatRooms() {

        return chatRoomRepository.findByIsGroupChat("Y").stream()
                .map(c -> new ChatRoomListResponseDto(
                        c.getId(),
                        c.getName())
                )
                .toList();
    }
}
