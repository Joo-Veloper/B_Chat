package io.chat.domain.chat.service;

import io.chat.domain.chat.dto.ChatMessageRequestDto;
import io.chat.domain.chat.dto.ChatMessageResponseDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final ReadStatusRepository readStatusRepository;

    @Transactional
    @Override
    public ChatRoomResponseDto createGroupRoom(String roomName) {

        Member member = getAuthenticatedMember();
        ChatRoom chatRoom = chatRoomRepository.save(
                ChatRoom.builder().name(roomName).isGroupChat("Y").build()
        );
        addParticipantToRoom(chatRoom, member);

        return new ChatRoomResponseDto(chatRoom.getId(), chatRoom.getName());
    }

    @Transactional
    @Override
    public void saveMessage(Long roomId, ChatMessageRequestDto chatMessageRequestDto) {

        ChatRoom chatRoom = getChatRoomById(roomId);
        Member sender = getMemberByEmail(chatMessageRequestDto.getSenderEmail());
        ChatMessage chatMessage = chatMessageRepository.save(
                ChatMessage.builder().chatRoom(chatRoom).member(sender).content(chatMessageRequestDto.getMessage()).build()
        );

        List<ReadStatus> readStatuses = createReadStatuses(chatRoom, chatMessage, sender);
        readStatusRepository.saveAll(readStatuses);
    }

    @Override
    public List<ChatRoomListResponseDto> getGroupChatRooms() {

        return chatRoomRepository.findByIsGroupChat("Y").stream()
                .map(c -> new ChatRoomListResponseDto(c.getId(), c.getName()))
                .toList();
    }

    @Transactional
    @Override
    public void addParticipantToGroupChat(Long roomId) {

        ChatRoom chatRoom = getChatRoomById(roomId);
        Member member = getAuthenticatedMember();

        chatParticipantRepository.findByChatRoomAndMember(chatRoom, member)
                .orElseGet(() -> addParticipantToRoom(chatRoom, member));
    }

    private ChatRoom getChatRoomById(Long roomId) {

        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));
    }

    private Member getMemberByEmail(String email) {

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }

    private Member getAuthenticatedMember() {

        return memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("Authenticated member not found"));
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

    private ChatParticipant addParticipantToRoom(ChatRoom chatRoom, Member member) {

        return chatParticipantRepository.save(
                ChatParticipant.builder().chatRoom(chatRoom).member(member).build()
        );
    }

    @Override
    public List<ChatMessageResponseDto> getChatHistory(Long roomId) {

        ChatRoom chatRoom = getChatRoomById(roomId);
        Member member = getAuthenticatedMember();

        if (!isRoomParticipant(member.getEmail(), roomId)) {
            throw new IllegalArgumentException("본인이 속하지 않은 채팅방입니다.");
        }

        return chatMessageRepository.findByChatRoomOrderByCreateTimeAsc(chatRoom)
                .stream()
                .map(c -> new ChatMessageResponseDto(c.getContent(), c.getMember().getEmail()))
                .toList();
    }

    @Override
    public Boolean isRoomParticipant(String email, Long roomId) {

        ChatRoom chatRoom = getChatRoomById(roomId);
        Member member = getMemberByEmail(email);

        return chatParticipantRepository.findByChatRoomAndMember(chatRoom, member).isPresent();
    }
}
