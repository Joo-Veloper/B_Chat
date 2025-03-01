package io.chat.global.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    /* 200 OK */
    /* CHAT */
    OK(200, "요청 성공"),
    SUCCESS_CHATROOM_CREATE(201, "채팅방 생성 성공");


    private final int httpStatus;

    private final String message;
}
