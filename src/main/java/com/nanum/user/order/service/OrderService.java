package com.nanum.user.order.service;

import com.nanum.global.common.dto.ApiResponse;
import com.nanum.global.error.ErrorCode;
import com.nanum.global.error.exception.BusinessException;
import com.nanum.user.member.model.Member;
import com.nanum.user.member.repository.MemberRepository;
import com.nanum.user.order.model.OrderMaster;
import com.nanum.user.order.model.OrderStatus;
import com.nanum.user.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createOrder(Long memberId) {
        // Placeholder for order creation logic
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        // Logic to create order from cart or direct purchase would go here

        return null; // Return order ID
    }

    public OrderMaster getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }
}
