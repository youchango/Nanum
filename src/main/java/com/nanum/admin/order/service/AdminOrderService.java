package com.nanum.admin.order.service;

import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.entity.ManagerType;
import com.nanum.admin.manager.service.CustomManagerDetails;
import com.nanum.domain.order.dto.AdminOrderSearchDTO;
import com.nanum.domain.order.dto.OrderDTO;
import com.nanum.domain.order.model.OrderDetail;
import com.nanum.domain.order.model.OrderMaster;
import com.nanum.domain.order.model.QOrderMaster;
import com.nanum.domain.payment.model.Payment;
import com.nanum.domain.payment.model.PaymentStatus;
import com.nanum.user.order.repository.OrderDetailRepository;
import com.nanum.user.order.repository.OrderRepository;
import com.nanum.user.payment.repository.PaymentRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentRepository paymentRepository;

    public Map<String, Object> getOrders(AdminOrderSearchDTO searchDTO) {
        Manager manager = getCurrentManager();
        
        BooleanBuilder builder = new BooleanBuilder();
        QOrderMaster qOrderMaster = QOrderMaster.orderMaster;

        // 권한 필터링
        if (manager.getMbType() == ManagerType.MASTER || manager.getMbType() == ManagerType.SCM) {
            if (StringUtils.hasText(searchDTO.getSiteCd())) {
                builder.and(qOrderMaster.siteCd.eq(searchDTO.getSiteCd()));
            }
        } else {
            builder.and(qOrderMaster.siteCd.eq(manager.getSiteCd()));
        }

        // 상태 필터링
        if (searchDTO.getStatus() != null) {
            builder.and(qOrderMaster.status.eq(searchDTO.getStatus()));
        }

        // 기간 필터링
        if (searchDTO.getStartDate() != null) {
            builder.and(qOrderMaster.createdAt.goe(searchDTO.getStartDate().atStartOfDay()));
        }
        if (searchDTO.getEndDate() != null) {
            builder.and(qOrderMaster.createdAt.loe(searchDTO.getEndDate().atTime(23, 59, 59)));
        }

        // 키워드 검색
        if (StringUtils.hasText(searchDTO.getSearchKeyword())) {
            String keyword = searchDTO.getSearchKeyword();
            String type = searchDTO.getSearchType();
            if ("orderNo".equals(type)) {
                builder.and(qOrderMaster.orderNo.contains(keyword));
            } else if ("orderName".equals(type)) {
                builder.and(qOrderMaster.orderName.contains(keyword));
            } else if ("receiverName".equals(type)) {
                builder.and(qOrderMaster.receiverName.contains(keyword));
            } else {
                // Default search
                builder.and(qOrderMaster.orderName.contains(keyword)
                        .or(qOrderMaster.orderNo.contains(keyword))
                        .or(qOrderMaster.receiverName.contains(keyword)));
            }
        }

        PageRequest pageRequest = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getRecordSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderMaster> orderPage = orderRepository.findAll(builder, pageRequest);

        List<OrderDTO.Response> orderList = orderPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("orderList", orderList);
        result.put("totalCount", orderPage.getTotalElements());

        return result;
    }

    public OrderDTO.DetailResponse getOrderDetail(Long id) {
        OrderMaster order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. ID: " + id));

        // 권한 체크
        Manager manager = getCurrentManager();
        if (manager.getMbType() != ManagerType.MASTER && !manager.getSiteCd().equals(order.getSiteCd())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        List<OrderDetail> items = orderDetailRepository.findByOrderId(order.getOrderId());

        // 결제 정보 조회
        String paymentMethodDesc = null;
        PaymentStatus paymentStatus = null;
        List<Payment> payments = paymentRepository.findByOrderMasterOrderIdAndSiteCdAndOrderNo(id, order.getSiteCd(), order.getOrderNo());
        if (!payments.isEmpty()) {
            Payment payment = payments.get(payments.size() - 1);
            paymentMethodDesc = payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null;
            paymentStatus = payment.getPaymentStatus();
        }

        return OrderDTO.DetailResponse.builder()
                .orderId(order.getOrderId())
                .orderNo(order.getOrderNo())
                .orderName(order.getOrderName())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .paymentPrice(order.getPaymentPrice())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .receiverDetail(order.getReceiverDetail())
                .receiverZipcode(order.getReceiverZipcode())
                .deliveryMemo(order.getDeliveryMemo())
                .memberCode(order.getMember() != null ? order.getMember().getMemberCode() : null)
                .trackingNumber(order.getTrackingNumber())
                .paymentMethod(paymentMethodDesc)
                .paymentStatus(paymentStatus)
                .createdAt(order.getCreatedAt())
                .items(items.stream().map(this::convertToDetailItemResponse).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public void updateStatus(Long id, OrderDTO.StatusUpdateRequest request) {
        OrderMaster order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. ID: " + id));
        
        Manager manager = getCurrentManager();
        if (manager.getMbType() != ManagerType.MASTER && !manager.getSiteCd().equals(order.getSiteCd())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        order.changeStatus(request.getStatus());
    }

    @Transactional
    public void confirmPayment(Long id) {
        OrderMaster order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. ID: " + id));

        // 권한 체크
        Manager manager = getCurrentManager();
        if (manager.getMbType() != ManagerType.MASTER && !manager.getSiteCd().equals(order.getSiteCd())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        if (order.getStatus() != com.nanum.domain.order.model.OrderStatus.PAYMENT_WAIT) {
            throw new IllegalStateException("결제대기 상태의 주문만 결제 처리가 가능합니다.");
        }

        // 1. 주문 상태 변경
        order.changeStatus(com.nanum.domain.order.model.OrderStatus.PREPARING);

        // 2. 결제 상태 변경
        List<Payment> payments = paymentRepository.findByOrderMasterOrderIdAndSiteCdAndOrderNo(id, order.getSiteCd(), order.getOrderNo());
        if (!payments.isEmpty()) {
            Payment payment = payments.get(payments.size() - 1);
            payment.setPaymentStatus(PaymentStatus.PAID);
            payment.setPaymentDate(java.time.LocalDateTime.now());
            paymentRepository.save(payment);
        }
    }

    @Transactional
    public void cancelOrder(Long id) {
        OrderMaster order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. ID: " + id));

        // 권한 체크
        Manager manager = getCurrentManager();
        if (manager.getMbType() != ManagerType.MASTER && !manager.getSiteCd().equals(order.getSiteCd())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        com.nanum.domain.order.model.OrderStatus currentStatus = order.getStatus();
        if (currentStatus != com.nanum.domain.order.model.OrderStatus.PAYMENT_WAIT && 
            currentStatus != com.nanum.domain.order.model.OrderStatus.PREPARING) {
            throw new IllegalStateException("결제대기 또는 배송준비 상태의 주문만 취소가 가능합니다.");
        }

        // 1. 주문 상태 변경
        order.changeStatus(com.nanum.domain.order.model.OrderStatus.CANCELLED);

        // 2. 결제 상태 변경 (해당 주문의 모든 결제 건 취소)
        List<Payment> payments = paymentRepository.findByOrderMasterOrderIdAndSiteCdAndOrderNo(id, order.getSiteCd(), order.getOrderNo());
        for (Payment payment : payments) {
            payment.setPaymentStatus(PaymentStatus.CANCELLED);
            paymentRepository.save(payment);
        }
    }

    private OrderDTO.Response convertToResponse(OrderMaster order) {
        return OrderDTO.Response.builder()
                .orderId(order.getOrderId())
                .orderNo(order.getOrderNo())
                .orderName(order.getOrderName())
                .totalPrice(order.getTotalPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .paymentPrice(order.getPaymentPrice())
                .status(order.getStatus())
                .memberCode(order.getMember() != null ? order.getMember().getMemberCode() : null)
                .receiverName(order.getReceiverName())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderDTO.OrderDetailResponse convertToDetailItemResponse(OrderDetail detail) {
        return OrderDTO.OrderDetailResponse.builder()
                .orderDetailId(detail.getId())
                .productId(detail.getProductId())
                .productName(detail.getProductName())
                .optionName(detail.getOptionName())
                .quantity(detail.getQuantity())
                .pricePerUnit(detail.getProductPrice().add(detail.getOptionPrice()))
                .totalPrice(detail.getTotalPrice())
                .reviewYn("Y".equals(detail.getReviewYn()))
                .build();
    }

    private Manager getCurrentManager() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomManagerDetails) {
            return ((CustomManagerDetails) principal).getManager();
        }
        throw new IllegalStateException("인증된 관리자 정보가 없습니다.");
    }
}
