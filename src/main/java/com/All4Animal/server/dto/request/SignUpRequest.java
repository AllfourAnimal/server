package com.All4Animal.server.dto.request;

import com.All4Animal.server.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "로그인 아이디는 필수입니다.")
    @Size(min = 4, max = 20, message = "로그인 아이디는 4자 이상 20자 이하입니다.")
    @Schema(description = "로그인 아이디", example = "all4animal")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하입니다.")
    @Schema(description = "비밀번호", example = "password1234")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    @Schema(description = "비밀번호 확인", example = "password1234")
    private String passwordConfirm;

    @NotBlank(message = "이름은 필수입니다.")
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호는 숫자 10~11자리여야 합니다.")
    @Schema(description = "전화번호", example = "01012345678")
    private String phone;

    @NotNull(message = "출생연도는 필수입니다.")
    @Min(value = 1900, message = "출생연도는 1900년 이상이어야 합니다.")
    @Max(value = 2026, message = "출생연도는 2100년 이하여야 합니다.")
    @Schema(description = "출생연도", example = "1998")
    private Integer birthYear;

    @NotBlank(message = "지역은 필수입니다.")
    @Pattern(
            regexp = "^[가-힣a-zA-Z0-9\\s]+$",
            message = "지역은 한글, 영문, 숫자, 공백만 입력할 수 있습니다."
    )
    @Schema(description = "거주 지역", example = "서울 마포구")
    private String location;

    @NotNull(message = "경험 여부는 필수입니다.")
    @Schema(description = "반려동물 양육 경험 여부", example = "true")
    private Boolean isExperience;

    @NotNull(message = "주거 형태는 필수입니다.")
    @Schema(description = "주거 형태", example = "APARTMENT_VILLA")
    private Users.Housing housingType;

    @NotNull(message = "평균 외출 시간은 필수입니다.")
    @Min(value = 0, message = "평균 외출 시간은 0시간 이상이어야 합니다.")
    @Max(value = 24, message = "평균 외출 시간은 24시간 이하여야 합니다.")
    @Schema(description = "하루 평균 집을 비우는 시간(시간 단위)", example = "6")
    private Integer emptyTime;
}
