package com.nanum.user.point.repository;

import com.nanum.user.point.dto.PointSearchDto;
import com.nanum.user.point.model.Point;
import com.nanum.user.point.model.QPoint;
import com.nanum.user.member.model.QMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Point> searchPoints(PointSearchDto searchDto, Pageable pageable) {
        QPoint point = QPoint.point;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();

        if (searchDto.getMemberCode() != null) {
            builder.and(point.member.memberCode.eq(searchDto.getMemberCode()));
        }

        if (StringUtils.hasText(searchDto.getMemberName())) {
            builder.and(point.member.memberName.contains(searchDto.getMemberName()));
        }

        if (searchDto.getStartDate() != null) {
            builder.and(point.createdAt.goe(searchDto.getStartDate().atStartOfDay()));
        }

        if (searchDto.getEndDate() != null) {
            builder.and(point.createdAt.loe(searchDto.getEndDate().atTime(23, 59, 59)));
        }

        List<Point> content = queryFactory
                .selectFrom(point)
                .leftJoin(point.member, member).fetchJoin()
                .where(builder)
                .orderBy(point.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(point.count())
                .from(point)
                .where(builder);

        Long total = countQuery.fetchOne();
        if (total == null)
            total = 0L;

        return new PageImpl<>(content, pageable, total);
    }
}
