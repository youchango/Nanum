package com.nanum.domain.inquiry.repository;

import com.nanum.domain.inquiry.dto.InquiryDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.nanum.domain.inquiry.model.QInquiry.inquiry;
import static com.nanum.domain.member.model.QMember.member;
import static com.nanum.domain.product.model.QProduct.product;
import static com.nanum.domain.order.model.QOrderMaster.orderMaster;
import static com.nanum.admin.manager.entity.QManager.manager;

@Repository
@RequiredArgsConstructor
public class InquiryRepositoryImpl implements InquiryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<InquiryDTO.Response> search(InquiryDTO.Search search, Pageable pageable) {
        List<InquiryDTO.Response> content = queryFactory
                .select(Projections.fields(InquiryDTO.Response.class,
                        inquiry.id,
                        inquiry.type,
                        inquiry.productId,
                        inquiry.orderNo,
                        inquiry.title,
                        inquiry.content,
                        inquiry.answer,
                        inquiry.status,
                        inquiry.writer.memberCode.as("writerCode"),
                        inquiry.writer.memberName.as("writerName"),
                        inquiry.siteCd,
                        inquiry.isSecret.as("isSecret"),
                        inquiry.createdAt,
                        inquiry.answeredAt,
                        manager.managerCode.as("answererCode"),
                        product.name.as("productName"),
                        orderMaster.orderName.as("orderName")))
                .from(inquiry)
                .leftJoin(inquiry.writer, member)
                .leftJoin(manager).on(inquiry.answererCode.eq(manager.managerCode))
                .leftJoin(product).on(inquiry.productId.eq(product.id))
                .leftJoin(orderMaster).on(inquiry.orderNo.eq(orderMaster.orderNo))
                .where(
                        typeEq(search),
                        statusEq(search),
                        productIdEq(search),
                        orderNoEq(search),
                        keywordLike(search),
                        writerCodeEq(search),
                        periodBetween(search),
                        siteCdEq(search))
                .orderBy(inquiry.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(
                        typeEq(search),
                        statusEq(search),
                        productIdEq(search),
                        orderNoEq(search),
                        keywordLike(search),
                        writerCodeEq(search),
                        periodBetween(search),
                        siteCdEq(search));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression siteCdEq(InquiryDTO.Search search) {
        return StringUtils.hasText(search.getSiteCd()) ? inquiry.siteCd.eq(search.getSiteCd()) : null;
    }

    private BooleanExpression typeEq(InquiryDTO.Search search) {
        return search.getType() != null ? inquiry.type.eq(search.getType()) : null;
    }

    private BooleanExpression statusEq(InquiryDTO.Search search) {
        return search.getStatus() != null ? inquiry.status.eq(search.getStatus()) : null;
    }

    private BooleanExpression productIdEq(InquiryDTO.Search search) {
        return search.getProductId() != null ? inquiry.productId.eq(search.getProductId()) : null;
    }

    private BooleanExpression orderNoEq(InquiryDTO.Search search) {
        return StringUtils.hasText(search.getOrderNo()) ? inquiry.orderNo.eq(search.getOrderNo()) : null;
    }

    private BooleanExpression keywordLike(InquiryDTO.Search search) {
        if (!StringUtils.hasText(search.getKeyword())) {
            return null;
        }
        return inquiry.title.contains(search.getKeyword())
                .or(inquiry.content.contains(search.getKeyword()));
    }

    private BooleanExpression writerCodeEq(InquiryDTO.Search search) {
        return StringUtils.hasText(search.getWriterCode()) ? inquiry.writer.memberCode.eq(search.getWriterCode())
                : null;
    }

    private BooleanExpression periodBetween(InquiryDTO.Search search) {
        if (search.getStartDate() == null || search.getEndDate() == null) {
            return null;
        }
        return inquiry.createdAt.between(
                search.getStartDate().atStartOfDay(),
                search.getEndDate().atTime(23, 59, 59, 999999999));
    }
}
