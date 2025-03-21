package io.chat.domain.chat.repository;

import io.chat.domain.chat.entity.ChatMessage;
import io.chat.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomOrderByCreateTimeAsc(ChatRoom chatRoom);
}
