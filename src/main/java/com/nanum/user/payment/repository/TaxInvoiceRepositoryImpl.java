package com.nanum.user.payment.repository;
 
import com.nanum.admin.payment.dto.AdminTaxInvoiceDTO;
import com.nanum.domain.member.model.QMember;
import com.nanum.domain.order.model.QOrderMaster;
import com.nanum.domain.payment.model.QTaxInvoice;
import com.nanum.domain.payment.model.QPayment;
import com.nanum.domain.payment.model.InvoiceStatus;
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
public class TaxInvoiceRepositoryImpl implements TaxInvoiceRepositoryCustom {
 
    private final JPAQueryFactory queryFactory;
 
    @Override
    public Page<AdminTaxInvoiceDTO> findAdminTaxInvoices(SearchDTO searchDTO, InvoiceStatus status, String siteCd, Pageable pageable) {
        QTaxInvoice taxInvoice = QTaxInvoice.taxInvoice;
        QPayment payment = QPayment.payment;
        QOrderMaster orderMaster = QOrderMaster.orderMaster;
        QMember member = QMember.member;
 
        List<AdminTaxInvoiceDTO> content = queryFactory
                .select(Projections.fields(AdminTaxInvoiceDTO.class,
                        taxInvoice.invoiceId,
                        payment.paymentId,
                        orderMaster.orderName,
                        orderMaster.orderNo,
                        orderMaster.siteCd,
                        member.memberName.as("ordererName"),
                        taxInvoice.companyName,
                        taxInvoice.businessRegNum,
                        taxInvoice.issueAmount,
                        taxInvoice.invoiceStatus,
                        taxInvoice.issueDate
                ))
                .from(taxInvoice)
                .leftJoin(taxInvoice.payment, payment)
                .leftJoin(payment.orderMaster, orderMaster)
                .leftJoin(payment.member, member)
                .where(
                        eqSiteCd(orderMaster, siteCd),
                        eqStatus(taxInvoice, status),
                        containsKeyword(orderMaster, member, searchDTO)
                )
                .orderBy(taxInvoice.invoiceId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
 
        Long total = queryFactory
                .select(taxInvoice.count())
                .from(taxInvoice)
                .leftJoin(taxInvoice.payment, payment)
                .leftJoin(payment.orderMaster, orderMaster)
                .leftJoin(payment.member, member)
                .where(
                        eqSiteCd(orderMaster, siteCd),
                        eqStatus(taxInvoice, status),
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
 
    private BooleanExpression eqStatus(QTaxInvoice taxInvoice, InvoiceStatus status) {
        if (status != null) {
            return taxInvoice.invoiceStatus.eq(status);
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
