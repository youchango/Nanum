package com.nanum.user.payment.repository;

import com.nanum.domain.payment.dto.PaymentSearchDto;
import com.nanum.domain.payment.model.PaymentMaster;
import com.nanum.domain.payment.model.PaymentStatus;
import com.nanum.domain.payment.model.QPaymentMaster;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.querydsl.core.types.Projections;
import com.nanum.admin.payment.dto.AdminPaymentDTO;
import com.nanum.global.common.dto.SearchDTO;
import com.nanum.domain.order.model.QOrderMaster;
import com.nanum.domain.member.model.QMember;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PaymentMaster> searchPayments(PaymentSearchDto paymentSearchDto, Pageable pageable) {
        QPaymentMaster paymentMaster = QPaymentMaster.paymentMaster;

        List<PaymentMaster> content = queryFactory
                .selectFrom(paymentMaster)
                .where(
                        paymentDateBetween(paymentSearchDto.getStartDate(), paymentSearchDto.getEndDate()),
                        paymentStatusEq(paymentSearchDto.getPaymentStatus()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(paymentMaster.paymentId.desc())
                .fetch();

        long total = queryFactory
                .selectFrom(paymentMaster)
                .where(
                        paymentDateBetween(paymentSearchDto.getStartDate(), paymentSearchDto.getEndDate()),
                        paymentStatusEq(paymentSearchDto.getPaymentStatus()))
                .fetch().size();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression paymentDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return QPaymentMaster.paymentMaster.paymentDate.between(
                LocalDateTime.of(startDate, LocalTime.MIN),
                LocalDateTime.of(endDate, LocalTime.MAX));
    }

    private BooleanExpression paymentStatusEq(PaymentStatus status) {
        if (status == null) {
            return null;
        }
        return QPaymentMaster.paymentMaster.paymentStatus.eq(status);
    }

    @Override
    public Page<AdminPaymentDTO> findAdminPayments(SearchDTO searchDTO, PaymentStatus status, String siteCd, Pageable pageable) {
        QPaymentMaster paymentMaster = QPaymentMaster.paymentMaster;
        QOrderMaster orderMaster = QOrderMaster.orderMaster;
        QMember member = QMember.member;

        List<AdminPaymentDTO> content = queryFactory
                .select(Projections.fields(AdminPaymentDTO.class,
                        paymentMaster.paymentId,
                        orderMaster.orderId.stringValue().as("orderNo"),
                        orderMaster.orderName,
                        member.memberName.as("ordererName"),
                        paymentMaster.paymentPrice,
                        paymentMaster.paymentMethod.stringValue().as("paymentMethod"),
                        paymentMaster.paymentStatus.stringValue().as("paymentStatus"),
                        paymentMaster.paymentDate,
                        orderMaster.siteCd
                ))
                .from(paymentMaster)
                .leftJoin(paymentMaster.orderMaster, orderMaster)
                .leftJoin(paymentMaster.member, member)
                .where(
                        eqSiteCd(siteCd, orderMaster),
                        paymentStatusEq(status),
                        containsAdminKeyword(searchDTO.getSearchType(), searchDTO.getKeyword(), orderMaster, member)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(paymentMaster.paymentId.desc())
                .fetch();

        long total = queryFactory
                .selectFrom(paymentMaster)
                .leftJoin(paymentMaster.orderMaster, orderMaster)
                .leftJoin(paymentMaster.member, member)
                .where(
                        eqSiteCd(siteCd, orderMaster),
                        paymentStatusEq(status),
                        containsAdminKeyword(searchDTO.getSearchType(), searchDTO.getKeyword(), orderMaster, member)
                )
                .fetch().size();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression eqSiteCd(String siteCd, QOrderMaster orderMaster) {
        if (!StringUtils.hasText(siteCd) || "ALL".equalsIgnoreCase(siteCd)) {
            return null;
        }
        return orderMaster.siteCd.eq(siteCd);
    }

    private BooleanExpression containsAdminKeyword(String searchType, String keyword, QOrderMaster orderMaster, QMember member) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        if ("orderName".equals(searchType)) {
            return orderMaster.orderName.containsIgnoreCase(keyword);
        } else if ("ordererName".equals(searchType)) {
            return member.memberName.containsIgnoreCase(keyword);
        } else if ("orderNo".equals(searchType)) {
            return orderMaster.orderId.stringValue().containsIgnoreCase(keyword);
        }
        return orderMaster.orderName.containsIgnoreCase(keyword)
                .or(member.memberName.containsIgnoreCase(keyword))
                .or(orderMaster.orderId.stringValue().containsIgnoreCase(keyword));
    }
}
