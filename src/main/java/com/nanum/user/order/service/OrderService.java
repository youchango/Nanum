package com.nanum.user.order.service;

import com.nanum.global.error.ErrorCode;
import com.nanum.global.error.exception.BusinessException;
import com.nanum.domain.member.model.Member;
import com.nanum.user.member.repository.MemberRepository;
import com.nanum.domain.order.model.OrderMaster;
import com.nanum.domain.order.model.OrderStatus;
import com.nanum.user.order.repository.OrderRepository;
import com.nanum.domain.order.dto.OrderDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

        private final OrderRepository orderRepository;
        private final MemberRepository memberRepository;

        @Transactional
        public Long createOrder(String memberCode, OrderDTO.CreateRequest request) {
                Member member = memberRepository.findByMemberCode(memberCode)
                                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

                OrderMaster order = OrderMaster.builder()
                                .orderNo(java.time.LocalDateTime.now()
                                                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                                                + "-" + java.util.UUID.randomUUID().toString().substring(0, 6))
                                .member(member)
                                .orderName("Order by " + member.getMemberName()) // Placeholder
                                .totalPrice(BigDecimal.ZERO) // Will calc below
                                .status(OrderStatus.PAYMENT_WAIT)
                                .receiverName(request.getReceiverName())
                                .receiverPhone(request.getReceiverPhone())
                                .receiverAddress(request.getReceiverAddress())
                                .receiverDetail(request.getReceiverDetail())
                                .receiverZipcode(request.getReceiverZipcode())
                                .deliveryMemo(request.getDeliveryMemo())
                                .build();

                // Items logic (Need OrderDetail repository and Product repository)
                // Simplified for now: Assume only OrderMaster is saved or Cascade types

                orderRepository.save(order);
                return Long.valueOf(order.getOrderId().hashCode()); // Placeholder ID conversion
        }

        @Transactional
        public void updateStatus(Long orderId, OrderDTO.StatusUpdateRequest request) {
                OrderMaster order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
                order.changeStatus(request.getStatus());
                // order.setTrackingNumber(request.getTrackingNumber()); // Add if needed
        }

        public java.util.List<OrderDTO.Response> getAllOrders() {
                return orderRepository.findAll().stream()
                                .map(o -> OrderDTO.Response.builder()
                                                .orderId(o.getOrderId())
                                                .status(o.getStatus())
                                                .receiverName(o.getReceiverName())
                                                .totalPrice(o.getTotalPrice())
                                                .build())
                                .collect(java.util.stream.Collectors.toList());
        }

        public OrderMaster getOrder(Long orderId) {
                return orderRepository.findById(orderId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        }
}
