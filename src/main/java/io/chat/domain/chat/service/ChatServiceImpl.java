package io.chat.domain.chat.service;

import io.chat.domain.chat.dto.*;
import io.chat.domain.chat.entity.*;
import io.chat.domain.chat.repository.*;
import io.chat.domain.member.entity.Member;
import io.chat.domain.member.repository.MemberRepository;
import io.chat.domain.chat.sse.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationService notificationService;

    /**
     * Creating a Group Chat Room
     */
    @Transactional
    @Override
    public ChatRoomResponseDto createGroupRoom(String roomName) {

        Member member = getAuthenticatedMember();
        ChatRoom chatRoom = createChatRoom(roomName);
        addParticipantToRoom(chatRoom, member);

        return new ChatRoomResponseDto(chatRoom.getId(), chatRoom.getName());
    }

    /**
     * Save chat messages
     */
    @Transactional
    @Override
    public void saveMessage(Long roomId, ChatMessageRequestDto chatMessageRequestDto) {

        ChatRoom chatRoom = getChatRoomById(roomId);
        Member sender = getMemberByEmail(chatMessageRequestDto.getSenderEmail());

        ChatMessage chatMessage = saveChatMessage(chatRoom, sender, chatMessageRequestDto.getMessage());
        saveReadStatuses(chatRoom, chatMessage, sender);
        notifyParticipants(chatRoom, sender);

    }

    /**
     * Group Chat Room List View
     */
    @Override
    public List<ChatRoomListResponseDto> getGroupChatRooms() {

        return chatRoomRepository.findByIsGroupChat("Y")
                .stream()
                .map(c -> new ChatRoomListResponseDto(c.getId(), c.getName()))
                .toList();
    }

    /**
     * Join the group chat room
     */
    @Transactional
    @Override
    public void addParticipantToGroupChat(Long roomId) {

        ChatRoom chatRoom = getChatRoomById(roomId);
        Member member = getAuthenticatedMember();

        chatParticipantRepository.findByChatRoomAndMember(chatRoom, member)
                .orElseGet(() -> addParticipantToRoom(chatRoom, member));
    }

    /**
     * Check Chat Room Message History
     */
    @Override
    public List<ChatMessageResponseDto> getChatHistory(Long roomId) {

        validateRoomParticipant(roomId);
        ChatRoom chatRoom = getChatRoomById(roomId);

        return chatMessageRepository.findByChatRoomOrderByCreateTimeAsc(chatRoom)
                .stream()
                .map(c -> new ChatMessageResponseDto(c.getContent(), c.getMember().getEmail()))
                .toList();
    }

    /**
     * Verify that the user is a chat room participant
     */
    @Override
    public Boolean isRoomParticipant(String email, Long roomId) {

        ChatRoom chatRoom = getChatRoomById(roomId);
        Member member = getMemberByEmail(email);

        return chatParticipantRepository.findByChatRoomAndMember(chatRoom, member).isPresent();
    }

    /**
     * Processing Chat Message Read
     */
    @Transactional
    @Override
    public void messageRead(Long roomId) {

        validateRoomParticipant(roomId);

        ChatRoom chatRoom = getChatRoomById(roomId);
        Member member = getAuthenticatedMember();

        readStatusRepository.findByChatRoomAndMember(chatRoom, member)
                .stream()
                .filter(readStatus -> !readStatus.isRead())
                .forEach(readStatus -> readStatus.markAsRead(true));
    }

    /**
     * Look up my chatroom list
     */
    @Override
    public List<MyChatListResponseDto> getMyChatRoom() {

        Member member = getAuthenticatedMember();

        return chatParticipantRepository.findAllByMember(member)
                .stream()
                .map(c -> new MyChatListResponseDto(
                        c.getChatRoom().getId(),
                        c.getChatRoom().getName(),
                        c.getChatRoom().getIsGroupChat(),
                        readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), member)))
                .collect(Collectors.toList());
    }

    /**
     * Leave the chat room
     */
    @Transactional
    @Override
    public void leaveChatRoom(Long roomId) {

        ChatRoom chatRoom = getChatRoomById(roomId);
        Member member = getAuthenticatedMember();

        if (!"Y".equals(chatRoom.getIsGroupChat())) {
            throw new IllegalArgumentException("단체 채팅방이 아닙니다.");
        }

        ChatParticipant participant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member)
                .orElseThrow(() -> new EntityNotFoundException("참여자를 찾을 수 없습니다."));
        chatParticipantRepository.delete(participant);

        if (chatParticipantRepository.findByChatRoom(chatRoom).isEmpty()) {
            chatRoomRepository.delete(chatRoom);
        }
    }

    /**
     * 1:1 chat
     */
    @Transactional
    @Override
    public Long getOrCreatePrivateRoom(Long otherMemberId) {

        Member member = getAuthenticatedMember();
        Member otherMember = getMemberById(otherMemberId);

        if (member.getId().equals(otherMemberId)) {
            throw new IllegalArgumentException("본인과는 채팅할 수 없습니다.");
        }

        return chatParticipantRepository.findExistingPrivateRoom(member.getId(), otherMember.getId())
                .map(ChatRoom::getId)
                .orElseGet(() -> createPrivateChatRoom(member, otherMember));
    }

    // ===================== Private Methods ===================== //

    private ChatRoom createChatRoom(String roomName) {

        return chatRoomRepository.save(ChatRoom.builder()
                .name(roomName)
                .isGroupChat("Y")
                .build());
    }

    private Long createPrivateChatRoom(Member member, Member otherMember) {

        ChatRoom newRoom = chatRoomRepository.save(ChatRoom.builder()
                .isGroupChat("N")
                .name(String.format("%s-%s", member.getName(), otherMember.getName()))
                .build());

        addParticipantToRoom(newRoom, member);
        addParticipantToRoom(newRoom, otherMember);

        return newRoom.getId();
    }

    private ChatMessage saveChatMessage(ChatRoom chatRoom, Member sender, String message) {

        return chatMessageRepository.save(ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .content(message)
                .build());
    }

    private void saveReadStatuses(ChatRoom chatRoom, ChatMessage chatMessage, Member sender) {

        List<ReadStatus> readStatuses = chatParticipantRepository.findByChatRoom(chatRoom)
                .stream()
                .map(chatParticipant -> ReadStatus.builder()
                        .chatRoom(chatRoom)
                        .member(chatParticipant.getMember())
                        .chatMessage(chatMessage)
                        .isRead(chatParticipant.getMember().equals(sender))
                        .build())
                .toList();

        readStatusRepository.saveAll(readStatuses);
    }

    private ChatParticipant addParticipantToRoom(ChatRoom chatRoom, Member member) {

        return chatParticipantRepository.save(ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build());
    }

    private void validateRoomParticipant(Long roomId) {

        Member member = getAuthenticatedMember();

        if (!isRoomParticipant(member.getEmail(), roomId)) {
            throw new IllegalArgumentException("본인이 속하지 않은 채팅방입니다.");
        }
    }

    private ChatRoom getChatRoomById(Long roomId) {

        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));
    }

    private Member getMemberByEmail(String email) {

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Member getAuthenticatedMember() {

        return memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("로그인된 사용자를 찾을 수 없습니다."));
    }

    private void notifyParticipants(ChatRoom chatRoom, Member sender) {
        chatParticipantRepository.findByChatRoom(chatRoom)
                .forEach(participant -> {
                    if (!participant.getMember().equals(sender)) {
                        notificationService.sendNotification(participant.getMember().getId(), "새로운 메시지가 도착했습니다!");
                    }
                });
    }
}