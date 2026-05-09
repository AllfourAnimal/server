package com.All4Animal.server.dto.response;

import com.All4Animal.server.entity.Users;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserProfileResponse {

    private String loginId;
    private String username;
    private String phone;
    private Integer birthYear;
    private String location;

    @JsonProperty("isExperience")
    private boolean experience;

    private Users.Housing housingType;
    private Integer emptyTime;
    private LocalDateTime createdAt;

    public static UserProfileResponse from(Users user) {
        return new UserProfileResponse(
                user.getLoginId(),
                user.getUsername(),
                user.getPhone(),
                user.getBirthYear(),
                user.getLocation(),
                user.isExperience(),
                user.getHousingType(),
                user.getEmptyTime(),
                user.getCreatedAt()
        );
    }
}
