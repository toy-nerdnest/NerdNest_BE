package com.server.domain.member.entity;

import com.server.domain.audit.Auditable;
import com.server.domain.blog.entity.Blog;
import com.server.domain.imageFile.entity.ImageFile;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Member extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    @Setter
    private String nickName;

    @Column
    @Setter
    private String about;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "image_file_id")
    private ImageFile imageFile;

}
