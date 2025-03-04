package io.chat.domain.chat.repository;

import io.chat.domain.chat.entity.ChatRoom;
import io.chat.domain.chat.entity.ReadStatus;
import io.chat.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, Long> {

    List<ReadStatus> findByChatRoomAndMember(ChatRoom chatRoom, Member member);
}
