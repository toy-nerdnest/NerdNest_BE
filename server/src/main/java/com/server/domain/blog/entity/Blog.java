package com.server.domain.blog.entity;

import com.server.domain.audit.Auditable;
import com.server.domain.category.entity.Category;
import com.server.domain.comment.entity.Comment;
import com.server.domain.imageFile.entity.ImageFile;
import com.server.domain.likes.entity.Likes;
import com.server.domain.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @Column(name = "like_count", nullable = false, columnDefinition = "integer default 0")
    private int likeCount;

    @Column(name = "comment_count", nullable = false, columnDefinition = "integer default 0")
    private int commentCount;

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
    Set<Likes> likes = new HashSet<>();

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL)
    List<Comment> commentList = new ArrayList<>();

}
