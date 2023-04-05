package com.server.domain.blog.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.domain.blog.entity.Blog;
import com.server.domain.blog.entity.QBlog;
import com.server.domain.member.entity.QMember;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Repository
public class BlogCustomRepositoryImpl implements BlogCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Blog> findBlogByMemberIdAndYearIn(Long memberId, int year) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QBlog qBlog = QBlog.blog;
        QMember qMember = QMember.member;
        LocalDateTime startDateTime = LocalDateTime.of(year, Month.JANUARY, 1, 0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(year, Month.DECEMBER, 31, 23, 59, 59);

        List<Blog> blogs = queryFactory.selectFrom(qBlog)
                .join(qBlog.member, qMember)
                .where(qBlog.createdAt.between(startDateTime, endDateTime)
                        .and(qMember.memberId.eq(memberId)))
                .fetch();

        return blogs;
    }
}
