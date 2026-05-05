package com.All4Animal.server.controller;

import com.All4Animal.server.dto.request.LoginRequest;
import com.All4Animal.server.dto.request.SignUpRequest;
import com.All4Animal.server.dto.response.CurrentUserResponse;
import com.All4Animal.server.dto.response.ErrorResponse;
import com.All4Animal.server.dto.response.LoginIdCheckResponse;
import com.All4Animal.server.dto.response.LoginResponse;
import com.All4Animal.server.dto.response.SignUpResponse;
import com.All4Animal.server.entity.Users;
import com.All4Animal.server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "아이디 체크 ", description = "아이디 중복확인 하는데에 사용하는 api")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "중복 확인",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginIdCheckResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "사용 가능",
                                            value = """
                                                    {
                                                      "available": true,
                                                      "message": "사용 가능한 아이디입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "중복",
                                            value = """
                                                    {
                                                      "available": false,
                                                      "message": "이미 사용 중인 아이디입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping("/checkId")
    public ResponseEntity<?> isLoginIdDuplicated(
            @Valid
            @Parameter(description = "중복 확인할 로그인 아이디", example = "all4animal")
            @RequestParam String loginId
    ){
        boolean duplicated = authService.isLoginIdDuplicated(loginId);
        if(duplicated){
            return ResponseEntity.ok(LoginIdCheckResponse.duplicated());
        }
        return ResponseEntity.ok(LoginIdCheckResponse.available());
    }

    @Operation(summary = "회원가입", description = "회원가입 성공 시 userId와 message를 발급합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignUpResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                    {
                      "userId": 1,
                      "loginId": "all4animal"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "duplicate_login_id",
                                            summary = "아이디 중복",
                                            value = """
                        {
                          "code": "INVALID_INPUT",
                          "message": "이미 사용 중인 아이디입니다."
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "password_mismatch",
                                            summary = "비밀번호 불일치",
                                            value = """
                        {
                          "code": "INVALID_INPUT",
                          "message": "비밀번호와 비밀번호 확인이 일치하지 않습니다."
                        }
                        """
                                    )
                            }
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SignUpRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "housing_apartment_villa",
                                    summary = "주거 형태: 아파트/빌라",
                                    value = """
                    {
                      "loginId": "all4animal",
                      "password": "password1234",
                      "passwordConfirm": "password1234",
                      "name": "홍길동",
                      "phone": "01012345678",
                      "birthYear": 1998,
                      "location": "서울 마포구",
                      "isExperience": true,
                      "housingType": "APARTMENT_VILLA",
                      "emptyTime": 6
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "housing_detached_house",
                                    summary = "주거 형태: 단독주택",
                                    value = """
                    {
                      "loginId": "animallover",
                      "password": "password1234",
                      "passwordConfirm": "password1234",
                      "name": "김영희",
                      "phone": "01087654321",
                      "birthYear": 1995,
                      "location": "경기도 용인시",
                      "isExperience": false,
                      "housingType": "DETACHED_HOUSE",
                      "emptyTime": 4
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "housing_house_with_yard",
                                    summary = "주거 형태: 마당이 있는 집",
                                    value = """
                    {
                      "loginId": "yardhome",
                      "password": "password1234",
                      "passwordConfirm": "password1234",
                      "name": "박민수",
                      "phone": "01055556666",
                      "birthYear": 1992,
                      "location": "강원도 춘천시",
                      "isExperience": true,
                      "housingType": "HOUSE_WITH_YARD",
                      "emptyTime": 3
                    }
                    """
                            )
                    }
            )
    )
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "마스터 회원가입", description = "최초 1회만 마스터 계정을 생성합니다. 이미 마스터 계정이 있으면 실패합니다.")
    @PostMapping("/master/signup")
    public ResponseEntity<?> signupMaster(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = authService.signupMaster(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "로그인", description = "로그인 성공 시 JWT Access Token을 발급합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                    {
                      "accessToken": "eyJhbGciOiJIUzI1NiJ9.example.jwt.token",
                      "userId": 1,
                      "loginId": "all4animal",
                      "username": "홍길동",
                      "role": "USER"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청값 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "validation_error",
                                    value = """
                    {
                      "code": "VALIDATION_ERROR",
                      "message": "요청값이 올바르지 않습니다.",
                      "errors": {
                        "loginId": "로그인 아이디는 필수입니다.",
                        "password": "비밀번호는 필수입니다."
                      }
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "unauthorized",
                                    value = """
                    {
                      "code": "UNAUTHORIZED",
                      "message": "아이디 또는 비밀번호가 올바르지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "현재 로그인 사용자 조회", description = "Authorization 토큰을 기준으로 현재 로그인한 사용자의 기본 정보를 반환합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "현재 사용자 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CurrentUserResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "userId": 1,
                                              "loginId": "all4animal",
                                              "username": "홍길동",
                                              "role": "USER"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getMe() {
        Users user = authService.getCurrentUser();
        return ResponseEntity.ok(CurrentUserResponse.from(user));
    }
}
