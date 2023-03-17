package com.server.domain.imageFile.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageFileResponseDto {
    private Long imageFileId;
    private String imageFileName;
    private String imageFileUrl;
}
