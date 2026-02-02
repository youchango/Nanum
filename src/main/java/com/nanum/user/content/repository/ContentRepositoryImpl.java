package com.nanum.user.content.repository;

import com.nanum.user.content.model.Content;
import com.nanum.user.content.model.QContent;
import com.nanum.user.member.model.QMember;
import com.nanum.user.code.model.QCode;
import com.nanum.global.common.dto.SearchDTO;
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
public class ContentRepositoryImpl implements ContentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Content> searchContents(SearchDTO searchDTO, Pageable pageable) {
        QContent content = QContent.content;
        QMember member = QMember.member;
        QCode code = QCode.code;

        BooleanBuilder builder = new BooleanBuilder();

        // deleted_yn = 'N'
        builder.and(content.deletedYn.eq("N"));

        // keyword (subject OR contentBody)
        if (StringUtils.hasText(searchDTO.getKeyword())) {
            String keyword = searchDTO.getKeyword();
            builder.and(content.subject.contains(keyword)
                    .or(content.contentBody.contains(keyword)));
        }

        // searchType (contentTypeCode)
        if (StringUtils.hasText(searchDTO.getSearchType())) {
            try {
                Integer typeCodeInt = Integer.parseInt(searchDTO.getSearchType());
                builder.and(content.contentTypeCode.eq(typeCodeInt));
            } catch (NumberFormatException e) {
                // Ignore invalid format
            }
        }

        // List Query
        List<Content> results = queryFactory
                .selectFrom(content)
                .leftJoin(content.creator, member).fetchJoin()
                .leftJoin(content.typeCode, code).fetchJoin()
                .where(builder)
                .orderBy(content.contentId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count Query
        JPAQuery<Long> countQuery = queryFactory
                .select(content.count())
                .from(content)
                .where(builder);

        Long total = countQuery.fetchOne();
        if (total == null)
            total = 0L;

        return new PageImpl<>(results, pageable, total);
    }
}
