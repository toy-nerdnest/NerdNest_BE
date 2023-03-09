package com.server.utils;

import com.server.domain.comment.entity.Comment;

import javax.persistence.AttributeConverter;

public class CommentStatusConverter implements AttributeConverter<Comment.CommentStatus, String> {

    @Override
    public String convertToDatabaseColumn(Comment.CommentStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public Comment.CommentStatus convertToEntityAttribute(String dbData) {
        return Comment.CommentStatus.ofCode(dbData);
    }
}
