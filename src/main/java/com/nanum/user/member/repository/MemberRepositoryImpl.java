package com.nanum.user.member.repository;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.domain.member.model.Member;
import com.nanum.domain.member.model.QMember;
import com.nanum.domain.member.model.MemberType;
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
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Member> searchMembers(SearchDTO searchDTO, Pageable pageable) {
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(member.withdrawYn.eq("N"));

        if (StringUtils.hasText(searchDTO.getKeyword())) {
            String keyword = searchDTO.getKeyword();
            builder.and(member.memberName.contains(keyword)
                    .or(member.memberId.contains(keyword)));
        }

        if (StringUtils.hasText(searchDTO.getSearchType()) && StringUtils.hasText(searchDTO.getKeyword())) {
            // 기존 searchType 로직 유지 (예: 이름, 아이디 등)
        }

        // New Filtering Logic
        if (StringUtils.hasText(searchDTO.getMemberType())) {
            try {
                builder.and(member.memberType.eq(MemberType.valueOf(searchDTO.getMemberType())));
            } catch (Exception e) {
                // Ignore
            }
        }

        if (StringUtils.hasText(searchDTO.getApplyYn())) {
            builder.and(member.applyYn.eq(searchDTO.getApplyYn()));
        }

        List<Member> content = queryFactory
                .selectFrom(member)
                .where(builder)
                .orderBy(member.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .where(builder);

        Long total = countQuery.fetchOne();
        if (total == null)
            total = 0L;

        return new PageImpl<>(content, pageable, total);
    }
}
