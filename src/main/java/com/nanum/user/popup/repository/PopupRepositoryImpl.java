package com.nanum.user.popup.repository;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.popup.model.Popup;
import com.nanum.user.popup.model.QPopup;
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
public class PopupRepositoryImpl implements PopupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Popup> searchPopups(SearchDTO searchDTO, Pageable pageable) {
        QPopup popup = QPopup.popup;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(popup.deleteYn.eq("N"));

        if (StringUtils.hasText(searchDTO.getKeyword())) {
            String keyword = searchDTO.getKeyword();
            builder.and(popup.title.contains(keyword)
                    .or(popup.contentHtml.contains(keyword)));
        }

        List<Popup> content = queryFactory
                .selectFrom(popup)
                .where(builder)
                .orderBy(popup.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(popup.count())
                .from(popup)
                .where(builder);

        Long total = countQuery.fetchOne();
        if (total == null)
            total = 0L;

        return new PageImpl<>(content, pageable, total);
    }
}
