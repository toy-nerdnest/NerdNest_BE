package com.server.domain.blog.entity;

import com.server.domain.audit.Auditable;
import com.server.domain.category.entity.Category;
import com.server.domain.imageFile.entity.ImageFile;
import com.server.domain.likes.entity.Like;
import com.server.domain.member.entity.Member;
import lombok.*;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.stereotype.Service;
import reactor.util.annotation.Nullable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Blog extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blogId;

    @Column(name = "blog_title", length = 30, nullable = false)
    private String blogTitle;

    @Lob
    @Column(name = "blog_content")
    private String blogContent;

    @Column(name = "title_image_url")
    private String titleImageUrl;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne
    @JoinColumn(name = "image_file_id")
    private ImageFile imageFile;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL)
    Set<Like> likes = new HashSet<>();
}
