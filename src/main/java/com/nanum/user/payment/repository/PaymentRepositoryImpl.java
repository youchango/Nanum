package com.nanum.user.payment.repository;

import com.nanum.user.payment.dto.PaymentSearchDto;
import com.nanum.user.payment.model.Payment;
import com.nanum.user.payment.model.QPayment;
import com.nanum.user.member.model.QMember;
import com.nanum.user.payment.model.PaymentStatus;
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
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Payment> searchPayments(PaymentSearchDto searchDto, Pageable pageable) {
        QPayment payment = QPayment.payment;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(searchDto.getMemberName())) {
            builder.and(payment.member.memberName.contains(searchDto.getMemberName()));
        }

        if (StringUtils.hasText(searchDto.getPaymentMethod())) {
            builder.and(payment.paymentMethod.eq(searchDto.getPaymentMethod()));
        }

        if (searchDto.getPaymentStatus() != null) {
            builder.and(payment.paymentStatus.eq(searchDto.getPaymentStatus()));
        }

        if (searchDto.getStartDate() != null) {
            builder.and(payment.paymentDate.goe(searchDto.getStartDate().atStartOfDay()));
        }

        if (searchDto.getEndDate() != null) {
            builder.and(payment.paymentDate.loe(searchDto.getEndDate().atTime(23, 59, 59)));
        }

        List<Payment> content = queryFactory
                .selectFrom(payment)
                .leftJoin(payment.member, member).fetchJoin()
                .where(builder)
                .orderBy(payment.paymentDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(payment.count())
                .from(payment)
                .where(builder);

        Long total = countQuery.fetchOne();
        if (total == null)
            total = 0L;

        return new PageImpl<>(content, pageable, total);
    }
}
