package com.All4Animal.server.service;

import com.All4Animal.server.dto.request.S3PresignedUrlResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public S3PresignedUrlResponse getPostS3Url(Long userId, String filename) {
        String key = "profile/" + userId + "/" + UUID.randomUUID() + "/" + filename;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .putObjectRequest(putObjectRequest)
                .build();

        String presignedUrl = s3Presigner.presignPutObject(presignRequest)
                .url()
                .toString();

        return S3PresignedUrlResponse.builder()
                .preSignedUrl(presignedUrl)
                .key(key)
                .build();
    }

    @Transactional
    public S3PresignedUrlResponse uploadFile(Long userId, MultipartFile file) {
        return uploadFile(userId, file, "profile");
    }

    @Transactional
    public S3PresignedUrlResponse uploadReviewImage(Long userId, MultipartFile file) {
        return uploadFile(userId, file, "review");
    }

    @Transactional
    public S3PresignedUrlResponse uploadAdoptationProofImage(Long userId, MultipartFile file) {
        return uploadFile(userId, file, "adoptation");
    }

    public String uploadAiAnimalImage(Long animalId, byte[] imageBytes) {
        String key = "animal-ai/" + animalId + "/" + UUID.randomUUID() + ".jpg";

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("image/jpeg")
                .contentLength((long) imageBytes.length)
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(imageBytes)
        );

        return key;
    }

    private S3PresignedUrlResponse uploadFile(Long userId, MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String key = createFileKey(directory, userId, file.getOriginalFilename());
        String contentType = StringUtils.hasText(file.getContentType())
                ? file.getContentType()
                : "application/octet-stream";

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();

        try {
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException exception) {
            throw new IllegalArgumentException("파일 업로드에 실패했습니다.");
        }

        return getGetS3Url(userId, key);
    }

    @Transactional
    public S3PresignedUrlResponse getGetS3Url(Long userId, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();

        String presignedUrl = s3Presigner.presignGetObject(presignRequest)
                .url()
                .toString();

        return S3PresignedUrlResponse.builder()
                .preSignedUrl(presignedUrl)
                .key(key)
                .build();
    }

    @Transactional
    public void deleteFile(String key) {
        if (!StringUtils.hasText(key)) {
            return;
        }

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private String createFileKey(String directory, Long userId, String originalFilename) {
        String filename = StringUtils.hasText(originalFilename) ? originalFilename : "file";
        String sanitizedFilename = filename.replaceAll("[\\\\/]", "_");

        return directory + "/" + userId + "/" + UUID.randomUUID() + "/" + sanitizedFilename;
    }
}
