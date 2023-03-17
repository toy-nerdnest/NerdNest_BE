package com.server.domain.imageFile.entity;

import com.server.domain.blog.entity.Blog;
import com.server.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageFileId;

    @Column(nullable = false)
    private String imageFileName;

    @Column(nullable = false)
    private String imageFileUrl;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "blog_id")
    private Blog blog;

    public void setMember(Member member) {
        this.member = member;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }
}
