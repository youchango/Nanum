package com.nanum.admin.payment.service;

import com.nanum.admin.payment.dto.AdminPaymentDTO;
import com.nanum.admin.payment.dto.AdminPaymentDetailDTO;
import com.nanum.domain.payment.model.PaymentMaster;
import com.nanum.domain.payment.model.PaymentStatus;
import com.nanum.domain.order.model.OrderMaster;
import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPaymentServiceImpl implements AdminPaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public Page<AdminPaymentDTO> getPaymentList(SearchDTO searchDTO, PaymentStatus status, String siteCd, Pageable pageable) {
        return paymentRepository.findAdminPayments(searchDTO, status, siteCd, pageable);
    }

    @Override
    public AdminPaymentDetailDTO getPaymentDetail(Long paymentId) {
        PaymentMaster payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. 결제ID: " + paymentId));

        OrderMaster order = payment.getOrderMaster();
        
        return AdminPaymentDetailDTO.builder()
                .paymentId(payment.getPaymentId())
                .orderNo(order != null ? order.getOrderId().toString() : null)
                .orderName(order != null ? order.getOrderName() : null)
                .ordererName(payment.getMember() != null ? payment.getMember().getMemberName() : null)
                .totalPrice(payment.getTotalPrice())
                .discountPrice(payment.getDiscountPrice())
                .usedPoint(payment.getUsedPoint())
                .usedCoupon(payment.getUsedCoupon())
                .deliveryPrice(payment.getDeliveryPrice())
                .paymentPrice(payment.getPaymentPrice())
                .paymentStatus(payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : null)
                .paymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null)
                .paymentDate(payment.getPaymentDate())
                .bankName(payment.getBankName())
                .bankAccountNum(payment.getBankAccountNum())
                .depositorName(payment.getDepositorName())
                .cancelTotalPrice(payment.getCancelTotalPrice())
                .cancelPointPrice(payment.getCancelPointPrice())
                .cancelCouponPrice(payment.getCancelCouponPrice())
                .siteCd(order != null ? order.getSiteCd() : null)
                .build();
    }
}
