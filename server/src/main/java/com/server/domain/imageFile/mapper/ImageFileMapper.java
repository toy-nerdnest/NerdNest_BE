package com.server.domain.imageFile.mapper;

import com.server.domain.imageFile.dto.ImageFileResponseDto;
import com.server.domain.imageFile.entity.ImageFile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageFileMapper {
    ImageFileResponseDto imageFileToImageFileResponseDto(ImageFile imageFile);
}
