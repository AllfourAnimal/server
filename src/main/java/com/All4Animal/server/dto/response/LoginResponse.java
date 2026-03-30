package com.All4Animal.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private Long userId;
    private String loginId;
    private String username;
    private String message;
}
