package com.nanum.user.banner.repository;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.banner.model.Banner;
import com.nanum.user.banner.model.QBanner;
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
public class BannerRepositoryImpl implements BannerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Banner> searchBanners(SearchDTO searchDTO, Pageable pageable) {
        QBanner banner = QBanner.banner;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(banner.deleteYn.eq("N"));

        if (StringUtils.hasText(searchDTO.getKeyword())) {
            String keyword = searchDTO.getKeyword();
            builder.and(banner.bannerName.contains(keyword)
                    .or(banner.linkUrl.contains(keyword)));
        }

        List<Banner> content = queryFactory
                .selectFrom(banner)
                .where(builder)
                .orderBy(banner.bannerType.asc(), banner.sortOrder.asc(), banner.bannerId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(banner.count())
                .from(banner)
                .where(builder);

        Long total = countQuery.fetchOne();
        if (total == null)
            total = 0L;

        return new PageImpl<>(content, pageable, total);
    }
}
