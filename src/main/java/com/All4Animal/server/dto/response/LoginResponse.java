package com.All4Animal.server.dto.response;

import com.All4Animal.server.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private Long userId;
    private String loginId;
    private String username;
    private Users.Role role;
}
