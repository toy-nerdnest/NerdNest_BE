package com.server.domain.comment.service;

import com.server.domain.blog.entity.Blog;
import com.server.domain.comment.dto.CommentResponseDto;
import com.server.domain.comment.entity.Comment;
import com.server.domain.comment.repository.CommentCustomRepositoryImpl;
import com.server.domain.comment.repository.CommentRepository;
import com.server.domain.member.entity.Member;
import com.server.exception.BusinessLogicException;
import com.server.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public Comment updateParentComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public Comment findComment(long commentId) {
        return findVerifiedComment(commentId);
    }

    @Transactional(readOnly = true)
    public List<Comment> findComments(Blog blog){
        return customRepository.findCommentByBlog(blog);
    }

    public void deleteComment(Comment comment) {
        // 자식이 있는 댓글이라면
        if(comment.getChildren().size() != 0) {
            // 삭제 상태로 변경
            comment.changeStatus(Comment.CommentStatus.DEAD);
        }
        // 자식이 없는 댓글이라면
        else {
            // 삭제 가능한 조상 댓글을 전부 삭제
            commentRepository.delete(getDeletableAncestorComment(comment));
        }
    }

    public Comment getDeletableAncestorComment(Comment comment) {
        // 자식이 없는 댓글 경우 -> 부모에 판단을 안했음
        // 너 자식이야? parent != null  == 부모가 있다
        // 근데 자식이 여러명이면 애초에 부모댓글 삭제 불가(살았던지 죽었던지)
        // 자식이 1개일 경우 삭제 가능
        // 근데 이때 조건은 부모도 죽은 상태여야함.
        // 이때 부모를 재귀로 돌림
        // 부모 댓글이면 parent == null, 이므로 return comment -> delete 부모댓글 삭제
        Comment parent = comment.getParent();
        // 1. 부모 댓글이 존재하고 2. 부모의 자식이 1개이며 3. 부모가 상태가 dead인 경우
        // 부모가 삭제될 수 있는 조건
        if(parent != null && parent.getChildren().size() == 1 && parent.getStatus() == Comment.CommentStatus.DEAD){
            // 재귀로 삭제할 조상을 모두 리턴한다
            return getDeletableAncestorComment(parent);
        }
        return comment;
    }

    @Transactional(readOnly = true)
    public Comment findVerifiedComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
    }

    public Long countAllCommentsByPost(Blog blog){
        return commentRepository.countByBlog(blog);
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

}
