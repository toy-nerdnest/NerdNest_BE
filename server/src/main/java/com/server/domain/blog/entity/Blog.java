package com.server.domain.blog.entity;

import com.server.domain.audit.Auditable;
import com.server.domain.category.entity.Category;
import com.server.domain.member.entity.Member;
import lombok.*;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.stereotype.Service;

import javax.persistence.*;

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

//    @ManyToOne
//    @JoinColumn(name = "member_id")
//    private Member member;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
