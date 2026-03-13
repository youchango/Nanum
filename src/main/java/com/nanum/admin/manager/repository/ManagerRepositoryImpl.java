package com.nanum.admin.manager.repository;

import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.entity.QManager;
import com.nanum.admin.manager.entity.ManagerType;
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
public class ManagerRepositoryImpl implements ManagerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Manager> searchManagers(SearchDTO searchDTO, Pageable pageable) {
        QManager manager = QManager.manager;

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(searchDTO.getUseYn())) {
            builder.and(manager.useYn.eq(searchDTO.getUseYn()));
        } else {
            // 기본값: 비활성화(삭제)된 계정은 보이지 않도록 함
            builder.and(manager.useYn.eq("Y").or(manager.useYn.isNull()));
        }

        if (StringUtils.hasText(searchDTO.getKeyword())) {
            String keyword = searchDTO.getKeyword();
            String searchType = searchDTO.getSearchType();

            if ("id".equals(searchType)) {
                builder.and(manager.managerId.contains(keyword));
            } else if ("name".equals(searchType)) {
                builder.and(manager.managerName.contains(keyword));
            } else if ("email".equals(searchType)) {
                builder.and(manager.managerEmail.contains(keyword));
            } else {
                // 전체 검색 (id + name + email)
                builder.and(manager.managerName.contains(keyword)
                        .or(manager.managerId.contains(keyword))
                        .or(manager.managerEmail.contains(keyword)));
            }
        }

        if (StringUtils.hasText(searchDTO.getApplyYn())) {
            builder.and(manager.applyYn.eq(searchDTO.getApplyYn()));
        }

        if (StringUtils.hasText(searchDTO.getManagerType())) {
            try {
                builder.and(manager.mbType.eq(ManagerType.valueOf(searchDTO.getManagerType())));
            } catch (Exception e) {
                // Ignore
            }
        }

        List<Manager> content = queryFactory
                .selectFrom(manager)
                .where(builder)
                .orderBy(manager.loginDate.desc().nullsLast(), manager.managerSeq.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(manager.count())
                .from(manager)
                .where(builder);

        Long total = countQuery.fetchOne();
        if (total == null)
            total = 0L;

        return new PageImpl<>(content, pageable, total);
    }
}
