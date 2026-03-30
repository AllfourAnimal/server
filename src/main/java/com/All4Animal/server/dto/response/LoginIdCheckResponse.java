package com.All4Animal.server.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginIdCheckResponse {

    private boolean available;
    private String message;

    public static LoginIdCheckResponse available() {
        return new LoginIdCheckResponse(true, "사용 가능한 아이디입니다.");
    }

    public static LoginIdCheckResponse duplicated() {
        return new LoginIdCheckResponse(false, "이미 사용 중인 아이디입니다.");
    }
}
