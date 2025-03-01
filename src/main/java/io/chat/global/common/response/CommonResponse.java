package io.chat.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommonResponse<T> {

    private final String msg;

    private final Integer statusCode;

    private final T data;

    public static <T> CommonResponse<T> of(String msg, Integer statusCode, T data) {

        return new CommonResponse<>(msg, statusCode, data);
    }

    public static <T> CommonResponse<T> of(ResponseCode responseCode, T data) {

        return new CommonResponse<>(
                responseCode.getMessage(),
                responseCode.getHttpStatus(),
                data
        );
    }

}
