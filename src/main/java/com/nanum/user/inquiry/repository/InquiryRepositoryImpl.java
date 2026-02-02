package com.nanum.user.inquiry.repository;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.inquiry.model.Inquiry;
import com.nanum.user.inquiry.model.QInquiry;
import com.nanum.user.member.model.QMember;
import com.nanum.user.code.model.QCode;
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
public class InquiryRepositoryImpl implements InquiryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Inquiry> searchInquiries(SearchDTO searchDTO, Pageable pageable) {
        QInquiry inquiry = QInquiry.inquiry;
        QMember member = QMember.member;
        QCode code = QCode.code;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(inquiry.deleteYn.eq("N"));

        if (StringUtils.hasText(searchDTO.getKeyword())) {
            String keyword = searchDTO.getKeyword();
            builder.and(inquiry.title.contains(keyword)
                    .or(inquiry.content.contains(keyword)));
        }

        if (StringUtils.hasText(searchDTO.getSearchType())) {
            try {
                builder.and(inquiry.inquiryStatus
                        .eq(com.nanum.user.inquiry.model.InquiryStatus.valueOf(searchDTO.getSearchType())));
            } catch (Exception e) {
                // Ignore invalid status
            }
        }

        // List Query
        List<Inquiry> content = queryFactory
                .selectFrom(inquiry)
                .leftJoin(inquiry.writer, member).fetchJoin()
                .leftJoin(inquiry.typeCode, code).fetchJoin()
                .where(builder)
                .orderBy(inquiry.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count Query
        JPAQuery<Long> countQuery = queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(builder);

        Long total = countQuery.fetchOne();
        if (total == null)
            total = 0L;

        return new PageImpl<>(content, pageable, total);
    }
}
