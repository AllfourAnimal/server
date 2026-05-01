package com.All4Animal.server.controller;

import com.All4Animal.server.dto.request.LoginRequest;
import com.All4Animal.server.dto.request.SignUpRequest;
import com.All4Animal.server.dto.response.ErrorResponse;
import com.All4Animal.server.dto.response.LoginIdCheckResponse;
import com.All4Animal.server.dto.response.LoginResponse;
import com.All4Animal.server.dto.response.SignUpResponse;
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
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = authService.signup(request);
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
                      "username": "홍길동"
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
}
