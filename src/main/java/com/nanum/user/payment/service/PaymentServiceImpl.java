package com.nanum.user.payment.service;
 
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import com.nanum.domain.member.model.Member;
import com.nanum.user.member.repository.MemberRepository;
import com.nanum.domain.payment.dto.PaymentDto;
import com.nanum.domain.payment.dto.PaymentSearchDto;
import com.nanum.domain.payment.model.Payment;
import com.nanum.domain.payment.model.PaymentMethod;
import com.nanum.domain.payment.model.PaymentStatus;
import com.nanum.user.payment.repository.PaymentRepository;
import com.nanum.domain.point.dto.PointDto;
import com.nanum.domain.point.dto.PointSearchDto;
import com.nanum.domain.point.model.Point;
import com.nanum.domain.point.model.PointType;
import com.nanum.user.point.repository.PointRepository;
 
import java.math.BigDecimal;
 
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
 
    private final PaymentRepository paymentRepository;
    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;
 
    @Override
    @Transactional
    public void createPayment(String memberCode, Integer amount) {
        // TODO: Update this method to handle Order creation or linking if needed.
        // For now, adapting to Payment creation.
        Member member = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
 
        Payment payment = Payment.builder()
                .member(member)
                .totalPrice(BigDecimal.valueOf(amount))
                .paymentPrice(BigDecimal.valueOf(amount))
                .usedPoint(BigDecimal.ZERO)
                .usedCoupon(BigDecimal.ZERO)
                .deliveryPrice(BigDecimal.ZERO)
                .discountPrice(BigDecimal.ZERO)
                .paymentStatus(PaymentStatus.PAYMENT_WAIT)
                // .paymentMethod(PaymentMethod.CARD) // Default or null?
                .build();
 
        paymentRepository.save(payment);
    }
 
    @Override
    @Transactional
    public void processPayment(Long paymentId, String method, Integer usedPoint) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
 
        if (PaymentStatus.PAID.equals(payment.getPaymentStatus())) {
            throw new IllegalStateException("Already paid");
        }
 
        BigDecimal pointUsage = BigDecimal.valueOf(usedPoint);
 
        // Logic for point usage
        if (usedPoint > 0) {
            // Check if user has enough points - Need logic to calculate total points
            // For now assume valid and save history
            Point point = Point.builder()
                    .member(payment.getMember())
                    .pointUse(-usedPoint)
                    .pointBigo("Payment usage for payment: " + paymentId)
                    .pointType(PointType.USE)
                    .orderNo(null) // Payment doesn't have orderNo readily available in this context, using
                                   // null as permitted
                    .build();
            pointRepository.save(point);
        }
 
        payment.setPaymentStatus(PaymentStatus.PAID);
        // payment.setPaymentMethod(PaymentMethod.valueOf(method)); // Need to handle
        // String to Enum conversion safely
        try {
            payment.setPaymentMethod(PaymentMethod.valueOf(method));
        } catch (IllegalArgumentException e) {
            // Handle invalid method or Default
        }
 
        payment.setUsedPoint(pointUsage);
        payment.setPaymentDate(java.time.LocalDateTime.now());
 
        // Point accumulation (e.g., 1%)
        /*
         * BigDecimal pointEarn =
         * payment.getPaymentPrice().multiply(BigDecimal.valueOf(0.01));
         * if (pointEarn.compareTo(BigDecimal.ZERO) > 0) {
         * Point pointAcc = Point.builder()
         * .member(payment.getMember())
         * .pointUse(pointEarn.intValue())
         * .pointBigo("Payment accumulation for payment: " + paymentId)
         * .payment(payment)
         * .build();
         * pointRepository.save(pointAcc);
         * }
         */
    }
 
    @Override
    @Transactional
    public void cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
 
        if (!PaymentStatus.PAID.equals(payment.getPaymentStatus())) {
            throw new IllegalStateException("Cannot cancel unpaid payment");
        }
 
        payment.setPaymentStatus(PaymentStatus.CANCELLED);
        payment.setCancelTotalPrice(payment.getPaymentPrice()); // Simple cancel logic
 
        // Refund points if used
        if (payment.getUsedPoint().compareTo(BigDecimal.ZERO) > 0) {
            Point pointRefund = Point.builder()
                    .member(payment.getMember())
                    .pointUse(payment.getUsedPoint().intValue())
                    .pointBigo("Refund for cancelled payment: " + paymentId)
                    .pointType(PointType.SAVE)
                    .orderNo(null)
                    .build();
            pointRepository.save(pointRefund);
        }
 
        // Revert accumulation?
    }
 
    @Override
    public Page<PaymentDto> searchPayments(PaymentSearchDto searchDto, Pageable pageable) {
        return paymentRepository.searchPayments(searchDto, pageable)
                .map(PaymentDto::new);
    }
 
    @Override
    public PaymentDto getPaymentDetail(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        return new PaymentDto(payment);
    }
 
    @Override
    public Page<PointDto> searchPoints(PointSearchDto searchDto, Pageable pageable) {
        return pointRepository.searchPoints(searchDto, pageable)
                .map(PointDto::new);
    }
}
