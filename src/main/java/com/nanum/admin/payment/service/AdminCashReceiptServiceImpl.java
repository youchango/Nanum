package com.nanum.admin.payment.service;
 
import com.nanum.admin.payment.dto.AdminCashReceiptDTO;
import com.nanum.admin.payment.dto.AdminCashReceiptDetailDTO;
import com.nanum.domain.payment.model.CashReceipt;
import com.nanum.domain.payment.model.ReceiptStatus;
import com.nanum.domain.order.model.OrderMaster;
import com.nanum.domain.payment.model.Payment;
import com.nanum.domain.member.model.Member;
import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.payment.repository.CashReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCashReceiptServiceImpl implements AdminCashReceiptService {
 
    private final CashReceiptRepository cashReceiptRepository;
 
    @Override
    public Page<AdminCashReceiptDTO> getCashReceiptList(SearchDTO searchDTO, ReceiptStatus status, String siteCd, Pageable pageable) {
        Page<AdminCashReceiptDTO> page = cashReceiptRepository.findAdminCashReceipts(searchDTO, status, siteCd, pageable);
        page.getContent().forEach(dto -> {
            if (dto.getReceiptType() != null) {
                dto.setReceiptTypeDesc(dto.getReceiptType().getDescription());
            }
            if (dto.getReceiptStatus() != null) {
                dto.setReceiptStatusDesc(dto.getReceiptStatus().getDescription());
            }
        });
        return page;
    }
 
    @Override
    public AdminCashReceiptDetailDTO getCashReceiptDetail(Long receiptId) {
        CashReceipt receipt = cashReceiptRepository.findById(receiptId)
                .orElseThrow(() -> new IllegalArgumentException("Cash receipt not found: " + receiptId));
        
        Payment payment = receipt.getPayment();
        OrderMaster order = payment != null ? payment.getOrderMaster() : null;
        Member member = payment != null ? payment.getMember() : null;
 
        return AdminCashReceiptDetailDTO.builder()
                .receiptId(receipt.getReceiptId())
                .paymentId(payment != null ? payment.getPaymentId() : null)
                .orderName(order != null ? order.getOrderName() : null)
                .orderNo(order != null ? order.getOrderNo() : null)
                .siteCd(order != null ? order.getSiteCd() : null)
                .ordererName(member != null ? member.getMemberName() : null)
                .receiptType(receipt.getReceiptType())
                .receiptTypeDesc(receipt.getReceiptType() != null ? receipt.getReceiptType().getDescription() : null)
                .identityNum(receipt.getIdentityNum())
                .issueAmount(receipt.getIssueAmount())
                .supplyValue(receipt.getSupplyValue())
                .vat(receipt.getVat())
                .receiptStatus(receipt.getReceiptStatus())
                .receiptStatusDesc(receipt.getReceiptStatus() != null ? receipt.getReceiptStatus().getDescription() : null)
                .receiptUrl(receipt.getReceiptUrl())
                .issueDate(receipt.getIssueDate())
                .build();
    }
}
