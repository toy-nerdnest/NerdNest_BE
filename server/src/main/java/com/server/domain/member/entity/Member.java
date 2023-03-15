package com.server.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.domain.audit.Auditable;
import com.server.domain.blog.entity.Blog;
import com.server.domain.category.entity.Category;
import com.server.domain.imageFile.entity.ImageFile;
import com.server.domain.likes.entity.Likes;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column(nullable = false, unique = true)
    private String nickName;

    @Column
    private String about;

    @Column
    private String profileImageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    public Member update(String nickName, String profileImageUrl, String password) {
        this.nickName = nickName;
        this.profileImageUrl = profileImageUrl;
        this.password = password;
        return this;
    }

    @OneToOne
    @JoinColumn(name = "image_file_id")
    private ImageFile imageFile;

    @OneToMany(mappedBy = "member", orphanRemoval = true)
    @JsonIgnore
    private List<Blog> blogs;

    @OneToMany(mappedBy = "member", orphanRemoval = true)
    private List<Category> categories;

    @OneToMany(mappedBy = "member", orphanRemoval = true)
    @JsonIgnore
    private List<Likes> likes;

}
