package io.chat.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyChatListResponseDto {

    private Long roomId;
    private String roomName;
    private String isGroupChat;
    private Long unReadCount;
}
