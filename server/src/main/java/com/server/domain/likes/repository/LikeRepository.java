package com.server.domain.likes.repository;

import com.server.domain.likes.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Likes, Long> {
}
