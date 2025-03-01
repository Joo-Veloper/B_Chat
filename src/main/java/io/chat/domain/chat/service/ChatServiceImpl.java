package io.chat.domain.chat.service;

import io.chat.domain.chat.dto.ChatMessageRequestDto;
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

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("room can not be found"));

        Member member = memberRepository.findByEmail(chatMessageRequestDto.getSenderEmail())
                .orElseThrow(() -> new EntityNotFoundException("member can not be found"));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(member)
                .content(chatMessageRequestDto.getMessage())
                .build();

        chatMessageRepository.save(chatMessage);

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for (ChatParticipant chatParticipant : chatParticipants) {
            ReadStatus readStatus = ReadStatus.builder()
                    .chatRoom(chatRoom)
                    .member(chatParticipant.getMember())
                    .chatMessage(chatMessage)
                    .isRead(chatParticipant.getMember().equals(member))
                    .build();

            readStatusRepository.save(readStatus);
        }
    }
}
