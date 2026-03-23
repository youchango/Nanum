package com.nanum.admin.payment.service;

import com.nanum.admin.payment.dto.AdminTaxInvoiceDTO;
import com.nanum.admin.payment.dto.AdminTaxInvoiceDetailDTO;
import com.nanum.domain.payment.model.TaxInvoice;
import com.nanum.domain.payment.model.InvoiceStatus;
import com.nanum.domain.order.model.OrderMaster;
import com.nanum.domain.payment.model.PaymentMaster;
import com.nanum.domain.member.model.Member;
import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.payment.repository.TaxInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminTaxInvoiceServiceImpl implements AdminTaxInvoiceService {

    private final TaxInvoiceRepository taxInvoiceRepository;

    @Override
    public Page<AdminTaxInvoiceDTO> getTaxInvoiceList(SearchDTO searchDTO, InvoiceStatus status, String siteCd, Pageable pageable) {
        return taxInvoiceRepository.findAdminTaxInvoices(searchDTO, status, siteCd, pageable);
    }

    @Override
    public AdminTaxInvoiceDetailDTO getTaxInvoiceDetail(Long invoiceId) {
        TaxInvoice invoice = taxInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Tax invoice not found: " + invoiceId));
        
        PaymentMaster payment = invoice.getPaymentMaster();
        OrderMaster order = payment != null ? payment.getOrderMaster() : null;
        Member member = payment != null ? payment.getMember() : null;

        return AdminTaxInvoiceDetailDTO.builder()
                .invoiceId(invoice.getInvoiceId())
                .paymentId(payment != null ? payment.getPaymentId() : null)
                .orderName(order != null ? order.getOrderName() : null)
                .orderNo(order != null ? order.getOrderNo() : null)
                .siteCd(order != null ? order.getSiteCd() : null)
                .ordererName(member != null ? member.getMemberName() : null)
                .companyName(invoice.getCompanyName())
                .ceoName(invoice.getCeoName())
                .businessRegNum(invoice.getBusinessRegNum())
                .businessType(invoice.getBusinessType())
                .businessClass(invoice.getBusinessClass())
                .companyAddress(invoice.getCompanyAddress())
                .email(invoice.getEmail())
                .issueAmount(invoice.getIssueAmount())
                .supplyValue(invoice.getSupplyValue())
                .vat(invoice.getVat())
                .invoiceStatus(invoice.getInvoiceStatus())
                .invoiceUrl(invoice.getInvoiceUrl())
                .issueDate(invoice.getIssueDate())
                .build();
    }
}
