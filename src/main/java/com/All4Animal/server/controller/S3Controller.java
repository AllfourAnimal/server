package com.All4Animal.server.controller;

import com.All4Animal.server.dto.request.S3PresignedUrlResponse;
import com.All4Animal.server.service.AuthService;
import com.All4Animal.server.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
@Tag(name = "S3", description = "S3 API")
public class S3Controller {
    private final S3Service s3Service;
    private final AuthService authService;

    @Operation(summary = "S3 파일 업로드", description = "multipart/form-data로 파일을 받아 S3에 업로드합니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<S3PresignedUrlResponse> uploadFile(
            @Parameter(description = "업로드할 파일")
            @RequestPart("file") MultipartFile file
    ) {
        Long userId = authService.getCurrentUserId();
        S3PresignedUrlResponse response = s3Service.uploadFile(userId, file);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "S3 업로드용 Presigned URL 발급", description = "클라이언트가 직접 S3에 업로드할 수 있는 URL을 발급합니다.")
    @GetMapping(value = "/posturl")
    public ResponseEntity<S3PresignedUrlResponse> getPostS3Url(@RequestParam String filename) {
        Long userId = authService.getCurrentUserId();
        S3PresignedUrlResponse getS3UrlDto = s3Service.getPostS3Url(userId, filename);
        return new ResponseEntity<>(getS3UrlDto, HttpStatusCode.valueOf(200));
    }

    @Operation(summary = "S3 조회용 Presigned URL 발급", description = "S3 객체 key로 조회 가능한 URL을 발급합니다.")
    @GetMapping(value = "/geturl")
    public ResponseEntity<S3PresignedUrlResponse> getGetS3Url(@RequestParam String key) {
        Long userId = authService.getCurrentUserId();
        S3PresignedUrlResponse getS3UrlDto = s3Service.getGetS3Url(userId, key);
        return new ResponseEntity<>(getS3UrlDto, HttpStatusCode.valueOf(200));
    }
}
