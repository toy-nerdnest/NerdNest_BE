package com.server.domain.imageFile.repository;

import com.server.domain.imageFile.entity.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
}
