package com.server.domain.comment.service;

import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.service.BlogService;
import com.server.domain.comment.dto.CommentResponseDto;
import com.server.domain.comment.entity.Comment;
import com.server.domain.comment.repository.CommentCustomRepositoryImpl;
import com.server.domain.comment.repository.CommentRepository;
import com.server.domain.member.entity.Member;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentCustomRepositoryImpl customRepository;
    private final BlogService blogService;


    public Comment createComment(Comment comment, Long parentId) {

        // 자식 댓글이라면
        if (parentId != null) {
            // comment가 자식
            // 부모댓글 유무 판단 후
            Comment parent = findVerifiedComment(parentId);
            // 자식 댓글에 부모 업데이트
            comment.updateParent(parent);
        }
        return commentRepository.save(comment);
    }

    public Comment updateComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public Comment findComment(long commentId) {
        return findVerifiedComment(commentId);
    }

    @Transactional(readOnly = true)
    public List<Comment> findComments(Blog blog) {
        return customRepository.findCommentByBlog(blog);
    }

    @Transactional(readOnly = true)
    public Comment findVerifiedComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }

    public void verifyOwner(long commentId, Member loginMember) {
        Long loginMemberId = loginMember.getMemberId();
        Long ownerId = findComment(commentId).getMember().getMemberId();
        if (loginMemberId != ownerId) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_AUTHORIZED);
        }
    }

    public List<CommentResponseDto> getLayeredComments(Blog blog) {
        List<Comment> commentList = findComments(blog);
        List<CommentResponseDto> result = new ArrayList<>();
        Map<Long, CommentResponseDto> map = new HashMap<>();

        commentList.stream().forEach(c -> {
            CommentResponseDto commentResponseDto = CommentResponseDto.convertCommentToResponseDto(c);
            // map <댓글Id, responseDto>
            map.put(c.getCommentId(), commentResponseDto);
            // 댓글이 부모가 있다면
            if (c.getParent() != null) {
                // 부모 댓글의 id의 responseDto를 조회한다음
                map.get(c.getParent().getCommentId())
                        // 부모 댓글 responseDto의 자식으로
                        .getChildren()
                        // rDto를 추가한다.
                        .add(commentResponseDto);
            }
            // 댓글이 최상위 댓글이라면
            else {
                // 그냥 result에 추가한다.
                result.add(commentResponseDto);
            }
        });

        return result;
    }

    public void deleteComment(Comment comment) {
        if (comment.getChildren().size() != 0) {
            comment.changeStatus(Comment.CommentStatus.DEAD);
        } else {
            commentRepository.delete(getDeletableAncestorComment(comment));
        }
    }

    public Comment getDeletableAncestorComment(Comment comment) {
        Comment parent = comment.getParent();

        if (parent != null && parent.getChildren().size() == 1 && parent.getStatus() == Comment.CommentStatus.DEAD) {
            return getDeletableAncestorComment(parent);
        }

        return comment;
    }

}
