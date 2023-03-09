package com.server.domain.comment.entity;

import com.server.domain.audit.Auditable;
import com.server.domain.blog.entity.Blog;
import com.server.domain.member.entity.Member;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import com.server.utils.CommentStatusConverter;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Lob
    @Column(name = "comment_content", nullable = false)
    @Setter
    private String commentContent;

    @Convert(converter = CommentStatusConverter.class)
    private CommentStatus status = CommentStatus.ALIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private Comment parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    public void updateParent(Comment parent){
        this.parent = parent;
        parent.getChildren().add(this);
    }

    public List<Comment> getChildren(){
        return this.children;
    }

    public void changeStatus(CommentStatus commentStatus) {
        this.status = commentStatus;
    }

    @Getter
    public enum CommentStatus {
        ALIVE("생존", "1"),
        DEAD("사망", "2");

        private String status;
        private String code;

        CommentStatus(String status, String code) {
            this.status = status;
            this.code = code;
        }

        public static CommentStatus ofCode(String code) {
            return Arrays.stream(CommentStatus.values())
                    .filter(v-> v.getCode().equals(code))
                    .findAny()
                    .orElseThrow(()-> new BusinessLogicException(ExceptionCode.COMMENT_STATUS_INVALID));
        }
    }

}
