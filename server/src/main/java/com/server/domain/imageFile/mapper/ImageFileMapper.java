package com.server.domain.imageFile.mapper;

import com.server.domain.imageFile.dto.ImageFileResponseDto;
import com.server.domain.imageFile.entity.ImageFile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ImageFileMapper {
    ImageFileResponseDto imageFileToImageFileResponseDto(ImageFile imageFile);
}
