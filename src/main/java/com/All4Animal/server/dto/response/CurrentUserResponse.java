package com.All4Animal.server.dto.response;

import com.All4Animal.server.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurrentUserResponse {

    private Long userId;
    private String loginId;
    private String username;

    public static CurrentUserResponse from(Users user) {
        return new CurrentUserResponse(
                user.getUserId(),
                user.getLoginId(),
                user.getUsername()
        );
    }
}
