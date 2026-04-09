package com.All4Animal.server.service;

import com.All4Animal.server.dto.request.LoginRequest;
import com.All4Animal.server.dto.response.LoginResponse;
import com.All4Animal.server.dto.request.SignUpRequest;
import com.All4Animal.server.dto.response.SignUpResponse;
import com.All4Animal.server.entity.Users;
import com.All4Animal.server.exception.AuthenticationFailedException;
import com.All4Animal.server.repository.UserRepository;
import com.All4Animal.server.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public boolean isLoginIdDuplicated(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    public SignUpResponse signup(SignUpRequest request) {

        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        Users user = Users.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getName())
                .phone(request.getPhone())
                .birthYear(request.getBirthYear())
                .location(request.getLocation())
                .isExperience(request.getIsExperience())
                .createdAt(LocalDateTime.now())
                .build();

        Users savedUser = userRepository.save(user);

        return new SignUpResponse(
                savedUser.getUserId(),
                savedUser.getLoginId()
        );
    }

    public LoginResponse login(LoginRequest request) {
        Users user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new AuthenticationFailedException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationFailedException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }


        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getLoginId());

        return new LoginResponse(
                accessToken,
                user.getUserId(),
                user.getLoginId(),
                user.getUsername()
        );
    }

    public Long getUserIdFromToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new AuthenticationFailedException("유효하지 않은 토큰입니다.");
        }

        Long userId = jwtTokenProvider.extractUserId(token);
        if (userId == null) {
            throw new AuthenticationFailedException("토큰에서 사용자 정보를 찾을 수 없습니다.");
        }

        return userId;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationFailedException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        String loginId = authentication.getName();
        if (loginId == null || loginId.isBlank() || "anonymousUser".equals(loginId)) {
            throw new AuthenticationFailedException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        return userRepository.findByLoginId(loginId)
                .map(Users::getUserId)
                .orElseThrow(() -> new AuthenticationFailedException("사용자 정보를 찾을 수 없습니다."));
    }
}
