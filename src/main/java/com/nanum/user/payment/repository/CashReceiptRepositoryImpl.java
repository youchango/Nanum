package com.nanum.user.payment.repository;

import com.nanum.admin.payment.dto.AdminCashReceiptDTO;
import com.nanum.domain.member.model.QMember;
import com.nanum.domain.order.model.QOrderMaster;
import com.nanum.domain.payment.model.QCashReceipt;
import com.nanum.domain.payment.model.QPaymentMaster;
import com.nanum.domain.payment.model.ReceiptStatus;
import com.nanum.global.common.dto.SearchDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class CashReceiptRepositoryImpl implements CashReceiptRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminCashReceiptDTO> findAdminCashReceipts(SearchDTO searchDTO, ReceiptStatus status, String siteCd, Pageable pageable) {
        QCashReceipt cashReceipt = QCashReceipt.cashReceipt;
        QPaymentMaster paymentMaster = QPaymentMaster.paymentMaster;
        QOrderMaster orderMaster = QOrderMaster.orderMaster;
        QMember member = QMember.member;

        List<AdminCashReceiptDTO> content = queryFactory
                .select(Projections.fields(AdminCashReceiptDTO.class,
                        cashReceipt.receiptId,
                        paymentMaster.paymentId,
                        orderMaster.orderName,
                        orderMaster.orderNo,
                        orderMaster.siteCd,
                        member.memberName.as("ordererName"),
                        cashReceipt.receiptType,
                        cashReceipt.identityNum,
                        cashReceipt.issueAmount,
                        cashReceipt.receiptStatus,
                        cashReceipt.issueDate
                ))
                .from(cashReceipt)
                .leftJoin(cashReceipt.paymentMaster, paymentMaster)
                .leftJoin(paymentMaster.orderMaster, orderMaster)
                .leftJoin(paymentMaster.member, member)
                .where(
                        eqSiteCd(orderMaster, siteCd),
                        eqStatus(cashReceipt, status),
                        containsKeyword(orderMaster, member, searchDTO)
                )
                .orderBy(cashReceipt.receiptId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(cashReceipt.count())
                .from(cashReceipt)
                .leftJoin(cashReceipt.paymentMaster, paymentMaster)
                .leftJoin(paymentMaster.orderMaster, orderMaster)
                .leftJoin(paymentMaster.member, member)
                .where(
                        eqSiteCd(orderMaster, siteCd),
                        eqStatus(cashReceipt, status),
                        containsKeyword(orderMaster, member, searchDTO)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression eqSiteCd(QOrderMaster orderMaster, String siteCd) {
        if (siteCd != null && !siteCd.isEmpty() && !"DEFAULT".equals(siteCd)) {
            return orderMaster.siteCd.eq(siteCd);
        }
        return null;
    }

    private BooleanExpression eqStatus(QCashReceipt cashReceipt, ReceiptStatus status) {
        if (status != null) {
            return cashReceipt.receiptStatus.eq(status);
        }
        return null;
    }

    private BooleanExpression containsKeyword(QOrderMaster orderMaster, QMember member, SearchDTO searchDTO) {
        if (searchDTO == null || searchDTO.getKeyword() == null || searchDTO.getKeyword().isEmpty()) {
            return null;
        }
        String keyword = searchDTO.getKeyword();
        String type = searchDTO.getSearchType();

        if ("orderName".equals(type)) {
            return orderMaster.orderName.containsIgnoreCase(keyword);
        } else if ("ordererName".equals(type)) {
            return member.memberName.containsIgnoreCase(keyword);
        } else if ("orderNo".equals(type)) {
            return orderMaster.orderNo.containsIgnoreCase(keyword);
        }
        return orderMaster.orderName.containsIgnoreCase(keyword)
                .or(member.memberName.containsIgnoreCase(keyword))
                .or(orderMaster.orderNo.containsIgnoreCase(keyword));
    }
}
