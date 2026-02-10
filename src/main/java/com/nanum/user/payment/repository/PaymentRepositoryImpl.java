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
}
