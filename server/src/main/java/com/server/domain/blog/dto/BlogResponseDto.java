package com.server.domain.blog.dto;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
public class BlogResponseDto {
    private String titleImageUrl;
    private String blogTitle;
    private String blogContent;
    private long categoryId;

}
