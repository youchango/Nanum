package com.nanum.user.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nanum.user.member.model.Member;
import com.nanum.user.member.repository.MemberRepository;
import com.nanum.user.payment.dto.PaymentDto;
import com.nanum.user.payment.dto.PaymentSearchDto;
import com.nanum.user.payment.model.Payment;
import com.nanum.user.payment.model.PaymentStatus;
import com.nanum.user.payment.repository.PaymentRepository;
import com.nanum.user.point.dto.PointDto;
import com.nanum.user.point.dto.PointSearchDto;
import com.nanum.user.point.model.Point;
import com.nanum.user.point.repository.PointRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillingServiceImpl implements BillingService {

    private final PaymentRepository paymentRepository;
    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void generateBill(Long memberId, Integer amount) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Payment payment = Payment.builder()
                .member(member)
                .paymentAmount(amount)
                .usedPoint(0)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod("NONE")
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

        // Logic for point usage
        if (usedPoint > 0) {
            // Check if user has enough points - Need logic to calculate total points
            // For now assume valid and save history
             Point pointUsage = Point.builder()
                    .member(payment.getMember())
                    .pointUse(-usedPoint)
                    .pointBigo("Payment usage for bill: " + paymentId)
                    .payment(payment)
                    .build();
            pointRepository.save(pointUsage);
        }

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentMethod(method);
        payment.setUsedPoint(usedPoint);
        payment.setPaymentDate(java.time.LocalDateTime.now());
        
        // Point accumulation (e.g., 1%)
        /*
        int pointEarn = (int) (payment.getPaymentAmount() * 0.01);
        if (pointEarn > 0) {
             Point pointAcc = Point.builder()
                    .member(payment.getMember())
                    .pointUse(pointEarn)
                    .pointBigo("Payment accumulation for bill: " + paymentId)
                    .payment(payment)
                    .build();
            pointRepository.save(pointAcc);
        }
        */
    }

    @Override
    @Transactional
    public void cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        
        if (!PaymentStatus.PAID.equals(payment.getPaymentStatus())) {
             throw new IllegalStateException("Cannot cancel unpaid bill");
        }

        payment.setPaymentStatus(PaymentStatus.CANCELLED);
        
        // Refund points if used
        if (payment.getUsedPoint() > 0) {
             Point pointRefund = Point.builder()
                    .member(payment.getMember())
                    .pointUse(payment.getUsedPoint())
                    .pointBigo("Refund for cancelled bill: " + paymentId)
                    .payment(payment)
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
