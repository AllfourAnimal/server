package com.All4Animal.server.controller;

import com.All4Animal.server.dto.request.LoginRequest;
import com.All4Animal.server.dto.request.SignUpRequest;
import com.All4Animal.server.dto.response.LoginIdCheckResponse;
import com.All4Animal.server.dto.response.LoginResponse;
import com.All4Animal.server.dto.response.SignUpResponse;
import com.All4Animal.server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
@SecurityRequirements
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "아이디 체크 ", description = "아이디 중복확인 하는데에 사용하는 api")
    @GetMapping("/checkId")
    public ResponseEntity<?> isLoginIdDuplicated(@RequestParam String loginId){
        boolean duplicated = authService.isLoginIdDuplicated(loginId);
        if(duplicated){
            return ResponseEntity.ok(LoginIdCheckResponse.duplicated());
        }else{

        }
        return ResponseEntity.ok(LoginIdCheckResponse.available());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "로그인", description = "로그인 성공 시 JWT Access Token을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
