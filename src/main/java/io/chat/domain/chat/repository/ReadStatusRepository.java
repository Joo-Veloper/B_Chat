package io.chat.domain.chat.repository;

import io.chat.domain.chat.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, Long> {

}
