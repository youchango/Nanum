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
import com.nanum.user.delivery.repository.DeliveryRepository;
import com.nanum.user.point.repository.PointRepository;
import com.nanum.domain.delivery.model.Delivery;
import com.nanum.domain.delivery.model.DeliveryStatus;
import com.nanum.domain.point.model.Point;
import com.nanum.domain.point.model.PointType;
import com.nanum.domain.product.repository.ProductOptionRepository;
import com.nanum.domain.product.repository.ProductRepository;
import com.nanum.admin.inout.service.AdminInoutService;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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
    private final DeliveryRepository deliveryRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final AdminInoutService adminInoutService;
    private final PointRepository pointRepository;

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
                .memo(order.getMemo())
                .paymentMethod(paymentMethodDesc)
                .paymentStatus(paymentStatus)
                .createdAt(order.getCreatedAt())
                .memo(order.getMemo())
                .items(items.stream().map(this::convertToDetailItemResponse).collect(Collectors.toList()))
                .payments(payments.stream().map(this::convertToPaymentResponse).collect(Collectors.toList()))
                .deliveries(deliveryRepository.findByOrderId(id).stream()
                        .map(this::convertToDeliveryResponse).collect(Collectors.toList()))
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
    public void updateMemo(Long id, OrderDTO.MemoUpdateRequest request) {
        OrderMaster order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. ID: " + id));
        
        Manager manager = getCurrentManager();
        if (manager.getMbType() != ManagerType.MASTER && !manager.getSiteCd().equals(order.getSiteCd())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        order.setMemo(request.getMemo());
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

        // 3. 가용재고 복원 (stock_quantity)
        List<OrderDetail> details = orderDetailRepository.findByOrderId(id);
        for (OrderDetail detail : details) {
            if (detail.getOptionId() != null) {
                // 옵션 재고 복구
                productOptionRepository.increaseStockQuantity(detail.getOptionId(), detail.getQuantity());
                // 상품 마스터 재고 복구 (합산 관리용)
                productRepository.increaseStockQuantity(detail.getProductId(), detail.getQuantity());
            } else {
                // 단품 재고 복구
                productRepository.increaseStockQuantity(detail.getProductId(), detail.getQuantity());
            }
        }
    }

    /**
     * 배송 송장 등록 및 실재고 차감
     */
    @Transactional
    public void registerDelivery(Long orderId, List<OrderDTO.DeliveryRegisterRequest> requests) {
        OrderMaster order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. ID: " + orderId));

        Manager manager = getCurrentManager();
        if (manager.getMbType() != ManagerType.MASTER && !manager.getSiteCd().equals(order.getSiteCd())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        for (OrderDTO.DeliveryRegisterRequest req : requests) {
            Delivery delivery = Delivery.builder()
                    .orderId(order.getOrderId())
                    .orderNo(order.getOrderNo())
                    .siteCd(order.getSiteCd())
                    .orderDetailId(req.getOrderDetailId())
                    .deliveryCorp(req.getDeliveryCorp())
                    .trackingNumber(req.getTrackingNumber())
                    .status(DeliveryStatus.SHIPPING)
                    .shippedAt(java.time.LocalDateTime.now())
                    .build();
            deliveryRepository.save(delivery);

            // 실재고 차감 (product_stock)
            // OrderDetail 정보를 통해 상품/옵션 ID 및 수량 확인
            OrderDetail detail = orderDetailRepository.findById(req.getOrderDetailId())
                    .orElseThrow(() -> new IllegalArgumentException("주문 상세 정보를 찾을 수 없습니다. ID: " + req.getOrderDetailId()));
            
            // updateInoutStock을 통해 실재고 차감 및 전시 재고 동기화
            // AdminInoutService의 updateInoutStock이 private이므로 이 로직을 직접 수행하거나 
            // AdminInoutService에 위임하는 방식 검토. 여기서는 직접 수행 로직을 AdminInoutService에 추가하거나 public 전환 필요.
            // 일단 직접 구현 로직을 AdminInoutService의 public 메서드로 호출한다고 가정하고 추가 작업 예정.
            adminInoutService.decreasePhysicalStock(detail.getProductId(), detail.getOptionId(), detail.getQuantity(), "ORDER");
        }

        // 주문 상태 변경 (배송중)
        order.changeStatus(com.nanum.domain.order.model.OrderStatus.SHIPPING);
    }

    /**
     * 배송 완료 처리
     */
    @Transactional
    public void completeDelivery(Long orderId) {
        OrderMaster order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. ID: " + orderId));

        Manager manager = getCurrentManager();
        if (manager.getMbType() != ManagerType.MASTER && !manager.getSiteCd().equals(order.getSiteCd())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        if (order.getStatus() != com.nanum.domain.order.model.OrderStatus.SHIPPING) {
            throw new IllegalStateException("배송중 상태의 주문만 완료 처리가 가능합니다.");
        }

        // 1. 주문 상태 변경
        order.changeStatus(com.nanum.domain.order.model.OrderStatus.DELIVERED);

        // 2. 배송 상태 변경 (모든 관련 배송 건 완료 처리)
        List<Delivery> deliveries = deliveryRepository.findByOrderId(orderId);
        for (Delivery delivery : deliveries) {
            delivery.complete();
            deliveryRepository.save(delivery);
        }

        // 3. 포인트 적립 처리
        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
        int totalPointAmount = details.stream()
                .mapToInt(d -> d.getPointAmount() != null ? d.getPointAmount() : 0)
                .sum();

        if (totalPointAmount > 0) {
            Point pointRecord = Point.builder()
                    .pointUse(totalPointAmount)
                    .pointBigo("주문 적립 (" + order.getOrderNo() + ")")
                    .pointType(PointType.SAVE)
                    .member(order.getMember())
                    .orderNo(order.getOrderNo())
                    .build();
            
            pointRecord.setSiteCd(order.getSiteCd()); // @Setter 사용
            
            pointRepository.save(pointRecord);
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
                .memo(order.getMemo())
                .build();
    }

    private OrderDTO.OrderDetailResponse convertToDetailItemResponse(OrderDetail detail) {
        BigDecimal unitPrice = detail.getProductPrice().add(
                detail.getOptionPrice() != null ? detail.getOptionPrice() : BigDecimal.ZERO);
        return OrderDTO.OrderDetailResponse.builder()
                .orderDetailId(detail.getId())
                .productId(detail.getProductId())
                .orderNo(detail.getOrderNo())
                .productName(detail.getProductName())
                .optionName(detail.getOptionName())
                .quantity(detail.getQuantity())
                .pricePerUnit(unitPrice)
                .totalPrice(detail.getTotalPrice())
                .reviewYn("Y".equals(detail.getReviewYn()))
                .build();
    }

    private OrderDTO.PaymentResponse convertToPaymentResponse(Payment p) {
        return OrderDTO.PaymentResponse.builder()
                .paymentId(p.getPaymentId())
                .totalPrice(p.getTotalPrice())
                .discountPrice(p.getDiscountPrice())
                .usedPoint(p.getUsedPoint())
                .usedCoupon(p.getUsedCoupon())
                .paymentPrice(p.getPaymentPrice())
                .paymentStatus(p.getPaymentStatus())
                .paymentMethod(p.getPaymentMethod() != null ? p.getPaymentMethod().name() : null)
                .bankName(p.getBankName())
                .bankAccountNum(p.getBankAccountNum())
                .depositorName(p.getDepositorName())
                .paymentDate(p.getPaymentDate())
                .build();
    }

    private OrderDTO.DeliveryResponse convertToDeliveryResponse(Delivery d) {
        return OrderDTO.DeliveryResponse.builder()
                .deliveryId(d.getId())
                .orderDetailId(d.getOrderDetailId())
                .deliveryCorp(d.getDeliveryCorp())
                .trackingNumber(d.getTrackingNumber())
                .status(d.getStatus() != null ? d.getStatus().name() : null)
                .shippedAt(d.getShippedAt())
                .completedAt(d.getCompletedAt())
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
