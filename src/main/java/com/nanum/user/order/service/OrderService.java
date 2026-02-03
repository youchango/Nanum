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
        public Long createOrder(String memberCode, com.nanum.user.order.dto.OrderDTO.CreateRequest request) {
                Member member = memberRepository.findByMemberCode(memberCode)
                                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

                OrderMaster order = OrderMaster.builder()
                                .member(member)
                                .orderName("Order by " + member.getMemberName()) // Placeholder
                                .totalAmount(0L) // Will calc below
                                .status(OrderStatus.PAYMENT_WAIT)
                                .recipientName(request.getRecipientName())
                                // .shippingAddress(...) // Add fields to OrderMaster if needed or use
                                // addressDetail
                                .build();

                long totalAmount = 0;

                // Items logic (Need OrderDetail repository and Product repository)
                // Simplified for now: Assume only OrderMaster is saved or Cascade types

                orderRepository.save(order);
                return Long.valueOf(order.getOrderId().hashCode()); // Placeholder ID conversion
        }

        @Transactional
        public void updateStatus(Long orderId, com.nanum.user.order.dto.OrderDTO.StatusUpdateRequest request) {
                OrderMaster order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
                order.changeStatus(request.getStatus());
                // order.setTrackingNumber(request.getTrackingNumber()); // Add if needed
        }

        public java.util.List<com.nanum.user.order.dto.OrderDTO.Response> getAllOrders() {
                return orderRepository.findAll().stream()
                                .map(o -> com.nanum.user.order.dto.OrderDTO.Response.builder()
                                                .orderId(o.getOrderId())
                                                .status(o.getStatus())
                                                .recipientName(o.getRecipientName())
                                                .totalAmount(o.getTotalAmount())
                                                .build())
                                .collect(java.util.stream.Collectors.toList());
        }

        public OrderMaster getOrder(Long orderId) {
                return orderRepository.findById(orderId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        }
}
