package com.All4Animal.server.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class S3PresignedUrlResponse {
    private String preSignedUrl;
    private String key;

    @Builder
    public S3PresignedUrlResponse(String preSignedUrl, String key) {
        this.preSignedUrl = preSignedUrl;
        this.key = key;
    }
}
