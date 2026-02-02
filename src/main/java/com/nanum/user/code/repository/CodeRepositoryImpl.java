package com.nanum.user.code.repository;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.code.model.Code;
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
public class CodeRepositoryImpl implements CodeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Code> searchCodes(SearchDTO searchDTO, Pageable pageable) {
        QCode code = QCode.code;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(code.deleteYn.eq("N"));

        if (StringUtils.hasText(searchDTO.getKeyword())) {
            String keyword = searchDTO.getKeyword();
            builder.and(code.codeName.contains(keyword)
                    .or(code.codeType.contains(keyword)));
        }

        if (StringUtils.hasText(searchDTO.getSearchType())) {
            builder.and(code.codeType.eq(searchDTO.getSearchType()));
        }

        List<Code> content = queryFactory
                .selectFrom(code)
                .where(builder)
                .orderBy(code.codeType.asc(), code.depth.asc(), code.codeId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(code.count())
                .from(code)
                .where(builder);

        Long total = countQuery.fetchOne();
        if (total == null)
            total = 0L;

        return new PageImpl<>(content, pageable, total);
    }
}
