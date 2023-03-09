package com.server.domain.likes.entity;

import com.server.domain.blog.entity.Blog;
import com.server.domain.member.entity.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private LikeStatus likeStatus = LikeStatus.LIKE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    public Like(Member member, Blog blog) {
        this.member = member;
        this.blog = blog;
    }


    public enum LikeStatus {
        LIKE("좋아요"),
        CANCEL("취소");
        @Getter
        public String status;

        LikeStatus(String status) {
            this.status = status;
        }
    }
}
