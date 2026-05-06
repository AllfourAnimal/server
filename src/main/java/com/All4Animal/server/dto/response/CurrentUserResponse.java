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
    private Users.Role role;

    public static CurrentUserResponse from(Users user) {
        Users.Role role = user.getRole() == null ? Users.Role.USER : user.getRole();
        return new CurrentUserResponse(
                user.getUserId(),
                user.getLoginId(),
                user.getUsername(),
                role
        );
    }
}
