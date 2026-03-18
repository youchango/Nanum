package com.nanum.user.order.service;

import com.nanum.global.error.ErrorCode;
import com.nanum.global.error.exception.BusinessException;
import com.nanum.domain.cart.repository.CartRepository;
import com.nanum.domain.coupon.model.MemberCoupon;
import com.nanum.domain.coupon.repository.MemberCouponRepository;
import com.nanum.domain.member.model.Member;
import com.nanum.domain.member.model.MemberRole;
import com.nanum.domain.order.dto.OrderDTO;
import com.nanum.domain.order.model.OrderDetail;
import com.nanum.domain.order.model.OrderMaster;
import com.nanum.domain.order.model.OrderStatus;
import com.nanum.domain.payment.model.PaymentMaster;
import com.nanum.domain.payment.model.PaymentMethod;
import com.nanum.domain.payment.model.PaymentStatus;
import com.nanum.domain.point.model.Point;
import com.nanum.domain.product.model.Product;
import com.nanum.domain.product.model.ProductStatus;
import com.nanum.domain.product.model.ProductOption;
import com.nanum.domain.product.model.ProductSite;
import com.nanum.domain.product.repository.ProductRepository;
import com.nanum.domain.product.repository.ProductSiteRepository;
import com.nanum.user.member.repository.MemberRepository;
import com.nanum.user.order.repository.OrderDetailRepository;
import com.nanum.user.order.repository.OrderRepository;
import com.nanum.user.payment.repository.PaymentRepository;
import com.nanum.user.point.repository.PointRepository;

import com.nanum.domain.order.model.OrderTemp;
import com.nanum.user.order.repository.OrderTempRepository;
import com.nanum.user.payment.service.PgPaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private static final String SITE_CD = "NANUM";
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("50000");
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("3000");

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductSiteRepository productSiteRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final PointRepository pointRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final com.nanum.domain.shop.repository.ShopInfoRepository shopInfoRepository;
    private final OrderTempRepository orderTempRepository;
    private final PgPaymentService pgPaymentService;
    private final ObjectMapper objectMapper;

    /**
     * 주문 생성 (Deprecated — PG 연동 시 prepareOrder + confirmOrder 사용 권장)
     * - 상품별 Role 기반 가격 조회 (ProductSite)
     * - 옵션 추가금 반영
     * - 배송비 계산 (5만원 이상 무료배송, 미만 3,000원)
     * - PaymentMaster PENDING 상태 생성
     * - 장바구니 항목 삭제
     */
    @Deprecated
    @Transactional
    public Long createOrder(String memberId, OrderDTO.CreateRequest request) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // 상품 일괄 조회 (N+1 방지)
        List<Long> productIds = request.getItems().stream()
                .map(OrderDTO.OrderDetailItem::getProductId)
                .collect(Collectors.toList());
        List<Product> products = productRepository.findAllByIdWithOptions(productIds);
        java.util.Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 사이트 가격 일괄 조회 (N+1 방지)
        java.util.Map<Long, ProductSite> siteMap = productSiteRepository.findByProductInAndSiteCd(products, SITE_CD)
                .stream()
                .collect(Collectors.toMap(ps -> ps.getProduct().getId(), ps -> ps, (a, b) -> a));

        // 주문 상세 항목 생성 및 가격 계산
        List<OrderDetail> orderDetails = new ArrayList<>();
        List<String> productNames = new ArrayList<>();
        List<Long> orderedProductIds = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (int i = 0; i < request.getItems().size(); i++) {
            OrderDTO.OrderDetailItem item = request.getItems().get(i);

            Product product = productMap.get(item.getProductId());
            if (product == null) {
                throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
            }

            // 상품 상태 검증
            if ("Y".equals(product.getDeleteYn())) {
                throw new BusinessException("'" + product.getName() + "' 상품은 삭제된 상품입니다.", ErrorCode.ENTITY_NOT_FOUND);
            }
            if (!"Y".equals(product.getApplyYn())) {
                throw new BusinessException("'" + product.getName() + "' 상품은 현재 판매 승인되지 않은 상품입니다.", ErrorCode.INVALID_INPUT_VALUE);
            }
            if (product.getStatus() != ProductStatus.SALE) {
                throw new BusinessException("'" + product.getName() + "' 상품은 현재 판매 중이 아닙니다.", ErrorCode.INVALID_INPUT_VALUE);
            }

            // Role 기반 가격 산출
            int basePrice = product.getRetailPrice() != null ? product.getRetailPrice() : 0;
            ProductSite siteInfo = siteMap.get(product.getId());
            if (siteInfo != null) {
                MemberRole role = member.getRole();
                if (role == MemberRole.ROLE_BIZ) {
                    basePrice = siteInfo.getAPrice().intValue();
                } else if (role == MemberRole.ROLE_VETERAN) {
                    basePrice = siteInfo.getCPrice().intValue();
                } else {
                    basePrice = siteInfo.getBPrice().intValue();
                }
            }

            // 옵션 추가금 및 옵션명
            int optionExtraPrice = 0;
            String optionName = null;
            if (item.getOptionId() != null) {
                ProductOption found = product.getOptions().stream()
                        .filter(opt -> opt.getId().equals(item.getOptionId()) && "Y".equals(opt.getUseYn()))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(
                                "'" + product.getName() + "' 상품에 존재하지 않는 옵션입니다.",
                                ErrorCode.INVALID_INPUT_VALUE));
                optionExtraPrice = found.getExtraPrice();
                optionName = found.getName1() + (found.getName2() != null ? " " + found.getName2() : "");
            }

            final int unitPrice = basePrice + optionExtraPrice;
            BigDecimal itemTotal = BigDecimal.valueOf((long) unitPrice * item.getQuantity());
            totalPrice = totalPrice.add(itemTotal);

            // 가용재고 차감 (원자적 처리)
            int updated = productRepository.decreaseStockQuantity(product.getId(), item.getQuantity());
            if (updated == 0) {
                throw new BusinessException(
                        "'" + product.getName() + "' 상품의 재고가 부족합니다.",
                        ErrorCode.INVALID_INPUT_VALUE);
            }

            productNames.add(product.getName());
            orderedProductIds.add(product.getId());

            OrderDetail detail = OrderDetail.builder()
                    .orderSeq(i + 1)
                    .siteCd(SITE_CD)
                    .productId(product.getId())
                    .optionId(item.getOptionId())
                    .productName(product.getName())
                    .optionName(optionName)
                    .productPrice(BigDecimal.valueOf(basePrice))
                    .optionPrice(BigDecimal.valueOf(optionExtraPrice))
                    .quantity(item.getQuantity())
                    .totalPrice(itemTotal)
                    .orderStatus(OrderStatus.PAYMENT_WAIT)
                    .build();

            orderDetails.add(detail);
        }

        // 배송비 계산
        BigDecimal deliveryPrice = totalPrice.compareTo(FREE_DELIVERY_THRESHOLD) >= 0
                ? BigDecimal.ZERO
                : DELIVERY_FEE;
        BigDecimal paymentPrice = totalPrice.add(deliveryPrice);

        // 포인트 사용 처리 (사이트 설정 기반 최대 사용 비율 적용)
        com.nanum.domain.shop.model.ShopInfo shopInfo = shopInfoRepository.findBySiteCd(SITE_CD);
        BigDecimal usedPointAmount = BigDecimal.ZERO;
        final int requestedPoint = request.getUsedPoint() != null ? request.getUsedPoint() : 0;
        if (requestedPoint > 0) {
            int currentBalance = pointRepository.calculateBalance(member.getMemberCode());
            if (currentBalance < requestedPoint) {
                throw new BusinessException("포인트 잔액이 부족합니다. (보유: " + currentBalance + "P, 요청: " + requestedPoint + "P)",
                        ErrorCode.INVALID_INPUT_VALUE);
            }

            // 최대 포인트 사용 비율 체크
            if (shopInfo != null && shopInfo.getShopSetProductUseMaxPoint() != null
                    && shopInfo.getShopSetProductUseMaxPoint().compareTo(BigDecimal.ZERO) > 0) {
                int maxUsablePoint = totalPrice.multiply(shopInfo.getShopSetProductUseMaxPoint())
                        .divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.FLOOR).intValue();
                if (requestedPoint > maxUsablePoint) {
                    throw new BusinessException("포인트는 주문 금액의 " + shopInfo.getShopSetProductUseMaxPoint().intValue()
                            + "%까지만 사용 가능합니다. (최대: " + maxUsablePoint + "P)",
                            ErrorCode.INVALID_INPUT_VALUE);
                }
            }

            usedPointAmount = BigDecimal.valueOf(requestedPoint);
            paymentPrice = paymentPrice.subtract(usedPointAmount);
        }

        // 쿠폰 사용 처리
        BigDecimal usedCouponAmount = BigDecimal.ZERO;
        MemberCoupon memberCoupon = null;
        if (request.getMemberCouponId() != null) {
            memberCoupon = memberCouponRepository.findByIdAndMemberMemberCode(request.getMemberCouponId(), member.getMemberCode())
                    .orElseThrow(() -> new BusinessException("유효하지 않은 쿠폰입니다.", ErrorCode.ENTITY_NOT_FOUND));

            if ("Y".equals(memberCoupon.getUsedYn())) {
                throw new BusinessException("이미 사용된 쿠폰입니다.", ErrorCode.INVALID_INPUT_VALUE);
            }

            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(memberCoupon.getCoupon().getValidStartDate()) || now.isAfter(memberCoupon.getCoupon().getValidEndDate())) {
                throw new BusinessException("쿠폰 유효기간이 아닙니다.", ErrorCode.INVALID_INPUT_VALUE);
            }

            if (memberCoupon.getCoupon().getMinOrderPrice() != null
                    && totalPrice.compareTo(BigDecimal.valueOf(memberCoupon.getCoupon().getMinOrderPrice())) < 0) {
                throw new BusinessException("최소 주문 금액(" + memberCoupon.getCoupon().getMinOrderPrice() + "원) 이상이어야 쿠폰을 사용할 수 있습니다.",
                        ErrorCode.INVALID_INPUT_VALUE);
            }

            // 할인 금액 계산
            int discount;
            if ("RATE".equals(memberCoupon.getCoupon().getDiscountType())) {
                discount = totalPrice.multiply(BigDecimal.valueOf(memberCoupon.getCoupon().getDiscountValue()))
                        .divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.FLOOR)
                        .intValue();
                if (memberCoupon.getCoupon().getMaxDiscount() != null && discount > memberCoupon.getCoupon().getMaxDiscount()) {
                    discount = memberCoupon.getCoupon().getMaxDiscount();
                }
            } else {
                discount = memberCoupon.getCoupon().getDiscountValue();
            }

            usedCouponAmount = BigDecimal.valueOf(discount);
            paymentPrice = paymentPrice.subtract(usedCouponAmount);
        }

        // 할인 합계가 결제금액을 초과하면 주문 거부
        if (paymentPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(
                    "포인트와 쿠폰 할인 합계가 결제 금액을 초과합니다. 사용 금액을 조정해주세요.",
                    ErrorCode.INVALID_INPUT_VALUE);
        }

        // 주문명 생성
        String orderName = productNames.get(0);
        if (productNames.size() > 1) {
            orderName += " 외 " + (productNames.size() - 1) + "건";
        }

        // OrderMaster 저장
        OrderMaster order = OrderMaster.builder()
                .orderNo(orderNo)
                .orderName(orderName)
                .siteCd(SITE_CD)
                .member(member)
                .status(OrderStatus.PAYMENT_WAIT)
                .totalPrice(totalPrice)
                .deliveryPrice(deliveryPrice)
                .paymentPrice(paymentPrice)
                .discountPrice(usedPointAmount.add(usedCouponAmount))
                .usedPoint(usedPointAmount)
                .usedCoupon(usedCouponAmount)
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .receiverAddress(request.getReceiverAddress())
                .receiverDetail(request.getReceiverDetail())
                .receiverZipcode(request.getReceiverZipcode())
                .deliveryMemo(request.getDeliveryMemo())
                .build();

        orderRepository.save(order);

        // OrderDetail에 orderId 세팅 후 일괄 저장
        for (OrderDetail detail : orderDetails) {
            detail.setOrderId(order.getOrderId());
        }
        orderDetailRepository.saveAll(orderDetails);

        // 포인트 차감 기록 생성
        if (requestedPoint > 0) {
            Point pointRecord = Point.builder()
                    .pointUse(requestedPoint)
                    .pointBigo("주문 사용 (" + orderNo + ")")
                    .pointGubun("USE")
                    .member(member)
                    .orderNo(orderNo)
                    .build();
            pointRepository.save(pointRecord);
        }

        // 쿠폰 사용 처리
        if (memberCoupon != null) {
            memberCoupon.markUsed(order.getOrderId());
        }

        // PaymentMaster 생성 (PENDING)
        PaymentMethod paymentMethod = parsePaymentMethod(request.getPaymentMethod());
        PaymentMaster payment = PaymentMaster.builder()
                .orderMaster(order)
                .member(member)
                .totalPrice(totalPrice)
                .discountPrice(usedPointAmount.add(usedCouponAmount))
                .usedPoint(usedPointAmount)
                .usedCoupon(usedCouponAmount)
                .deliveryPrice(deliveryPrice)
                .paymentPrice(paymentPrice)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(paymentMethod)
                .build();
        paymentRepository.save(payment);

        // 장바구니 항목 삭제 (주문한 상품들)
        deleteCartItemsForOrder(member, orderedProductIds);

        log.info("주문 생성 완료 - orderNo: {}, memberId: {}, paymentPrice: {}", orderNo, memberId, paymentPrice);
        return order.getOrderId();
    }

    /**
     * 내 주문 목록 조회
     */
    public org.springframework.data.domain.Page<OrderDTO.Response> getMyOrders(String memberId, String startDate, String endDate, org.springframework.data.domain.Pageable pageable) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        org.springframework.data.domain.Page<OrderMaster> orderPage;
        if (startDate != null && endDate != null && !startDate.isBlank() && !endDate.isBlank()) {
            LocalDateTime start = java.time.LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime end = java.time.LocalDate.parse(endDate).plusDays(1).atStartOfDay();
            orderPage = orderRepository.findByMemberMemberCodeAndCreatedAtBetweenOrderByCreatedAtDesc(
                    member.getMemberCode(), start, end, pageable);
        } else {
            orderPage = orderRepository.findByMemberMemberCodeOrderByCreatedAtDesc(member.getMemberCode(), pageable);
        }
        List<OrderMaster> orders = orderPage.getContent();

        // OrderDetail 일괄 조회 (N+1 방지)
        List<Long> orderIds = orders.stream().map(OrderMaster::getOrderId).collect(Collectors.toList());
        List<OrderDetail> allDetails = orderIds.isEmpty() ? List.of() : orderDetailRepository.findByOrderIdIn(orderIds);
        java.util.Map<Long, List<OrderDetail>> detailMap = allDetails.stream()
                .collect(Collectors.groupingBy(OrderDetail::getOrderId));

        List<OrderDTO.Response> responseList = orders.stream().map(order -> {
            List<OrderDetail> details = detailMap.getOrDefault(order.getOrderId(), List.of());
            List<OrderDTO.OrderDetailResponse> items = details.stream()
                    .map(this::toOrderDetailResponse)
                    .collect(Collectors.toList());

            return OrderDTO.Response.builder()
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
                    .createdAt(order.getCreatedAt())
                    .items(items)
                    .build();
        }).collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(responseList, pageable, orderPage.getTotalElements());
    }

    /**
     * 주문 상세 조회 (본인 주문만)
     */
    public OrderDTO.DetailResponse getOrderDetail(Long orderId, String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        OrderMaster order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        // 본인 주문 검증
        if (!order.getMember().getMemberCode().equals(member.getMemberCode())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
        List<OrderDTO.OrderDetailResponse> items = details.stream()
                .map(this::toOrderDetailResponse)
                .collect(Collectors.toList());

        // 결제 정보 조회
        String paymentMethodDesc = null;
        PaymentStatus paymentStatus = null;
        PaymentMaster payment = paymentRepository.findByOrderMasterOrderId(orderId).orElse(null);
        if (payment != null) {
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
                .trackingNumber(order.getTrackingNumber())
                .paymentMethod(paymentMethodDesc)
                .paymentStatus(paymentStatus)
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    /**
     * 주문 상태 변경
     */
    @Transactional
    public void updateStatus(Long orderId, OrderDTO.StatusUpdateRequest request) {
        OrderMaster order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        order.changeStatus(request.getStatus());
    }

    /**
     * 주문 취소 (본인 주문, 결제대기/결제완료 상태만 가능)
     * - 주문 상태 → CANCELLED
     * - 결제 상태 → CANCELLED
     * - 가용재고 복원
     */
    @Transactional
    public void cancelOrder(Long orderId, String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        OrderMaster order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        // 본인 주문 검증
        if (!order.getMember().getMemberCode().equals(member.getMemberCode())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 취소 가능 상태 검증
        if (order.getStatus() != OrderStatus.PAYMENT_WAIT && order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(
                    "배송 준비 이후에는 주문을 취소할 수 없습니다. 1:1 문의를 이용해주세요.",
                    ErrorCode.INVALID_INPUT_VALUE);
        }

        // 주문 상태 변경
        order.changeStatus(OrderStatus.CANCELLED);

        // 결제 상태 변경
        PaymentMaster payment = paymentRepository.findByOrderMasterOrderId(orderId).orElse(null);
        if (payment != null) {
            payment.setPaymentStatus(PaymentStatus.CANCELLED);
        }

        // 가용재고 복원
        List<OrderDetail> details = orderDetailRepository.findByOrderId(orderId);
        for (OrderDetail detail : details) {
            productRepository.increaseStockQuantity(detail.getProductId(), detail.getQuantity());
        }

        // 포인트 복원
        if (order.getUsedPoint() != null && order.getUsedPoint().compareTo(BigDecimal.ZERO) > 0) {
            Point pointRestore = Point.builder()
                    .pointUse(order.getUsedPoint().intValue())
                    .pointBigo("주문 취소 복원 (" + order.getOrderNo() + ")")
                    .pointGubun("SAVE")
                    .member(member)
                    .orderNo(order.getOrderNo())
                    .build();
            pointRepository.save(pointRestore);
        }

        // 쿠폰 복원
        if (order.getUsedCoupon() != null && order.getUsedCoupon().compareTo(BigDecimal.ZERO) > 0) {
            memberCouponRepository.findByOrderIdAndUsedYn(order.getOrderId(), "Y")
                    .ifPresent(mc -> {
                        mc.setUsedYn("N");
                        mc.setUsedAt(null);
                        mc.setOrderId(null);
                    });
        }

        // PG 환불
        if (payment != null && payment.getPaymentKey() != null) {
            pgPaymentService.refund(payment.getPaymentKey(), order.getPaymentPrice());
        }

        log.info("주문 취소 완료 - orderId: {}, memberId: {}", orderId, memberId);
    }

    // --- PG 연동: 주문 준비 (OrderTemp 생성) ---

    /**
     * 주문 준비 — 가격 계산 후 OrderTemp에 저장, PG 결제 화면에 필요한 정보 반환.
     * 재고 차감/포인트/쿠폰 등 실 처리는 하지 않음.
     */
    @Transactional
    public OrderDTO.PrepareResponse prepareOrder(String memberId, OrderDTO.PrepareRequest request) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // 상품 일괄 조회
        List<Long> productIds = request.getItems().stream()
                .map(OrderDTO.OrderDetailItem::getProductId)
                .collect(Collectors.toList());
        List<Product> products = productRepository.findAllByIdWithOptions(productIds);
        java.util.Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 사이트 가격 일괄 조회
        java.util.Map<Long, ProductSite> siteMap = productSiteRepository.findByProductInAndSiteCd(products, SITE_CD)
                .stream()
                .collect(Collectors.toMap(ps -> ps.getProduct().getId(), ps -> ps, (a, b) -> a));

        // 가격 계산 (재고 차감 없이)
        List<String> productNames = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderDTO.OrderDetailItem item : request.getItems()) {
            Product product = productMap.get(item.getProductId());
            if (product == null) {
                throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
            }
            validateProductForOrder(product);

            int basePrice = resolveBasePrice(product, siteMap.get(product.getId()), member);
            int optionExtraPrice = resolveOptionExtraPrice(product, item.getOptionId());
            int unitPrice = basePrice + optionExtraPrice;
            BigDecimal itemTotal = BigDecimal.valueOf((long) unitPrice * item.getQuantity());
            totalPrice = totalPrice.add(itemTotal);
            productNames.add(product.getName());
        }

        // 배송비
        BigDecimal deliveryPrice = totalPrice.compareTo(FREE_DELIVERY_THRESHOLD) >= 0
                ? BigDecimal.ZERO : DELIVERY_FEE;
        BigDecimal paymentPrice = totalPrice.add(deliveryPrice);

        // 포인트 사전 검증
        final int requestedPoint = request.getUsedPoint() != null ? request.getUsedPoint() : 0;
        if (requestedPoint > 0) {
            validatePointUsage(member, requestedPoint, totalPrice);
            paymentPrice = paymentPrice.subtract(BigDecimal.valueOf(requestedPoint));
        }

        // 쿠폰 사전 검증
        if (request.getMemberCouponId() != null) {
            int couponDiscount = calculateCouponDiscount(member, request.getMemberCouponId(), totalPrice);
            paymentPrice = paymentPrice.subtract(BigDecimal.valueOf(couponDiscount));
        }

        if (paymentPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(
                    "포인트와 쿠폰 할인 합계가 결제 금액을 초과합니다. 사용 금액을 조정해주세요.",
                    ErrorCode.INVALID_INPUT_VALUE);
        }

        // 주문명
        String orderName = productNames.get(0);
        if (productNames.size() > 1) {
            orderName += " 외 " + (productNames.size() - 1) + "건";
        }

        // items JSON 스냅샷
        String itemsJson;
        try {
            itemsJson = objectMapper.writeValueAsString(request.getItems());
        } catch (JsonProcessingException e) {
            throw new BusinessException("주문 상품 직렬화 실패", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // OrderTemp 저장
        OrderTemp orderTemp = OrderTemp.builder()
                .orderNo(orderNo)
                .memberCode(member.getMemberCode())
                .siteCd(SITE_CD)
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .receiverAddress(request.getReceiverAddress())
                .receiverDetail(request.getReceiverDetail())
                .receiverZipcode(request.getReceiverZipcode())
                .deliveryMemo(request.getDeliveryMemo())
                .paymentMethod(request.getPaymentMethod())
                .usedPoint(requestedPoint)
                .memberCouponId(request.getMemberCouponId())
                .totalPrice(totalPrice)
                .deliveryPrice(deliveryPrice)
                .paymentPrice(paymentPrice)
                .itemsJson(itemsJson)
                .status("PENDING")
                .build();
        orderTempRepository.save(orderTemp);

        log.info("주문 준비 완료 - orderNo: {}, memberId: {}, paymentPrice: {}", orderNo, memberId, paymentPrice);

        return OrderDTO.PrepareResponse.builder()
                .orderNo(orderNo)
                .orderName(orderName)
                .totalPrice(totalPrice)
                .deliveryPrice(deliveryPrice)
                .paymentPrice(paymentPrice)
                .build();
    }

    // --- PG 연동: 결제 승인 후 주문 확정 ---

    /**
     * PG 결제 승인 확인 후 실제 주문 생성.
     * OrderTemp → PG 승인 → 재고 차감 / 포인트 / 쿠폰 / OrderMaster 생성.
     */
    @Transactional
    public Long confirmOrder(String memberId, OrderDTO.ConfirmRequest request) {
        // 1. OrderTemp 조회
        OrderTemp orderTemp = orderTempRepository.findByOrderNoAndStatus(request.getOrderNo(), "PENDING")
                .orElseThrow(() -> new BusinessException("유효하지 않은 주문이거나 이미 처리된 주문입니다.", ErrorCode.ENTITY_NOT_FOUND));

        // 2. 회원 검증
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        if (!orderTemp.getMemberCode().equals(member.getMemberCode())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 3. 만료 검증 (30분)
        if (orderTemp.getCreatedAt().plusMinutes(30).isBefore(LocalDateTime.now())) {
            orderTemp.setStatus("EXPIRED");
            throw new BusinessException("주문 유효시간이 초과되었습니다. 다시 주문해주세요.", ErrorCode.INVALID_INPUT_VALUE);
        }

        // 4. PG 결제 승인
        PgPaymentService.PgConfirmResult pgResult = pgPaymentService.confirm(
                request.getPaymentKey(), orderTemp.getOrderNo(), orderTemp.getPaymentPrice());
        if (!pgResult.isSuccess()) {
            throw new BusinessException("결제 승인 실패: " + pgResult.getErrorMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 5. items 복원
        List<OrderDTO.OrderDetailItem> items;
        try {
            items = objectMapper.readValue(orderTemp.getItemsJson(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new BusinessException("주문 상품 복원 실패", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 6. 상품/사이트 가격 조회
        List<Long> productIds = items.stream()
                .map(OrderDTO.OrderDetailItem::getProductId)
                .collect(Collectors.toList());
        List<Product> products = productRepository.findAllByIdWithOptions(productIds);
        java.util.Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        java.util.Map<Long, ProductSite> siteMap = productSiteRepository.findByProductInAndSiteCd(products, SITE_CD)
                .stream()
                .collect(Collectors.toMap(ps -> ps.getProduct().getId(), ps -> ps, (a, b) -> a));

        // 7. 주문 상세 생성 + 재고 차감
        List<OrderDetail> orderDetails = new ArrayList<>();
        List<String> productNames = new ArrayList<>();
        List<Long> orderedProductIds = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (int i = 0; i < items.size(); i++) {
            OrderDTO.OrderDetailItem item = items.get(i);
            Product product = productMap.get(item.getProductId());
            if (product == null) {
                pgPaymentService.refund(request.getPaymentKey(), orderTemp.getPaymentPrice());
                throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
            }

            validateProductForOrder(product);

            int basePrice = resolveBasePrice(product, siteMap.get(product.getId()), member);
            int optionExtraPrice = resolveOptionExtraPrice(product, item.getOptionId());
            String optionName = resolveOptionName(product, item.getOptionId());
            int unitPrice = basePrice + optionExtraPrice;
            BigDecimal itemTotal = BigDecimal.valueOf((long) unitPrice * item.getQuantity());
            totalPrice = totalPrice.add(itemTotal);

            // 재고 차감
            int updated = productRepository.decreaseStockQuantity(product.getId(), item.getQuantity());
            if (updated == 0) {
                pgPaymentService.refund(request.getPaymentKey(), orderTemp.getPaymentPrice());
                throw new BusinessException(
                        "'" + product.getName() + "' 상품의 재고가 부족합니다.",
                        ErrorCode.INVALID_INPUT_VALUE);
            }

            productNames.add(product.getName());
            orderedProductIds.add(product.getId());

            OrderDetail detail = OrderDetail.builder()
                    .orderSeq(i + 1)
                    .siteCd(SITE_CD)
                    .productId(product.getId())
                    .optionId(item.getOptionId())
                    .productName(product.getName())
                    .optionName(optionName)
                    .productPrice(BigDecimal.valueOf(basePrice))
                    .optionPrice(BigDecimal.valueOf(optionExtraPrice))
                    .quantity(item.getQuantity())
                    .totalPrice(itemTotal)
                    .orderStatus(OrderStatus.PAID)
                    .build();
            orderDetails.add(detail);
        }

        // 8. 배송비 / 결제금액 계산
        BigDecimal deliveryPrice = totalPrice.compareTo(FREE_DELIVERY_THRESHOLD) >= 0
                ? BigDecimal.ZERO : DELIVERY_FEE;
        BigDecimal paymentPrice = totalPrice.add(deliveryPrice);

        // 포인트 처리
        BigDecimal usedPointAmount = BigDecimal.ZERO;
        final int requestedPoint = orderTemp.getUsedPoint();
        if (requestedPoint > 0) {
            validatePointUsage(member, requestedPoint, totalPrice);
            usedPointAmount = BigDecimal.valueOf(requestedPoint);
            paymentPrice = paymentPrice.subtract(usedPointAmount);
        }

        // 쿠폰 처리
        BigDecimal usedCouponAmount = BigDecimal.ZERO;
        MemberCoupon memberCoupon = null;
        if (orderTemp.getMemberCouponId() != null) {
            int couponDiscount = calculateCouponDiscount(member, orderTemp.getMemberCouponId(), totalPrice);
            usedCouponAmount = BigDecimal.valueOf(couponDiscount);
            paymentPrice = paymentPrice.subtract(usedCouponAmount);
            memberCoupon = memberCouponRepository.findByIdAndMemberMemberCode(
                    orderTemp.getMemberCouponId(), member.getMemberCode()).orElse(null);
        }

        if (paymentPrice.compareTo(BigDecimal.ZERO) < 0) {
            pgPaymentService.refund(request.getPaymentKey(), orderTemp.getPaymentPrice());
            throw new BusinessException(
                    "포인트와 쿠폰 할인 합계가 결제 금액을 초과합니다.",
                    ErrorCode.INVALID_INPUT_VALUE);
        }

        // 주문명
        String orderName = productNames.get(0);
        if (productNames.size() > 1) {
            orderName += " 외 " + (productNames.size() - 1) + "건";
        }

        // 9. OrderMaster 저장
        OrderMaster order = OrderMaster.builder()
                .orderNo(orderTemp.getOrderNo())
                .orderName(orderName)
                .siteCd(SITE_CD)
                .member(member)
                .status(OrderStatus.PAID)
                .totalPrice(totalPrice)
                .deliveryPrice(deliveryPrice)
                .paymentPrice(paymentPrice)
                .discountPrice(usedPointAmount.add(usedCouponAmount))
                .usedPoint(usedPointAmount)
                .usedCoupon(usedCouponAmount)
                .receiverName(orderTemp.getReceiverName())
                .receiverPhone(orderTemp.getReceiverPhone())
                .receiverAddress(orderTemp.getReceiverAddress())
                .receiverDetail(orderTemp.getReceiverDetail())
                .receiverZipcode(orderTemp.getReceiverZipcode())
                .deliveryMemo(orderTemp.getDeliveryMemo())
                .build();
        orderRepository.save(order);

        // OrderDetail 저장
        for (OrderDetail detail : orderDetails) {
            detail.setOrderId(order.getOrderId());
        }
        orderDetailRepository.saveAll(orderDetails);

        // 포인트 차감
        if (requestedPoint > 0) {
            Point pointRecord = Point.builder()
                    .pointUse(requestedPoint)
                    .pointBigo("주문 사용 (" + orderTemp.getOrderNo() + ")")
                    .pointGubun("USE")
                    .member(member)
                    .orderNo(orderTemp.getOrderNo())
                    .build();
            pointRepository.save(pointRecord);
        }

        // 쿠폰 사용
        if (memberCoupon != null) {
            memberCoupon.markUsed(order.getOrderId());
        }

        // 10. PaymentMaster 생성 (PAID, paymentKey 포함)
        PaymentMethod paymentMethod = parsePaymentMethod(orderTemp.getPaymentMethod());
        PaymentMaster payment = PaymentMaster.builder()
                .orderMaster(order)
                .member(member)
                .totalPrice(totalPrice)
                .discountPrice(usedPointAmount.add(usedCouponAmount))
                .usedPoint(usedPointAmount)
                .usedCoupon(usedCouponAmount)
                .deliveryPrice(deliveryPrice)
                .paymentPrice(paymentPrice)
                .paymentStatus(PaymentStatus.PAID)
                .paymentMethod(paymentMethod)
                .build();
        payment.setPaymentKey(request.getPaymentKey());
        paymentRepository.save(payment);

        // 장바구니 삭제
        deleteCartItemsForOrder(member, orderedProductIds);

        // 11. OrderTemp 완료 처리
        orderTemp.setStatus("COMPLETED");

        log.info("주문 확정 완료 - orderNo: {}, memberId: {}, paymentPrice: {}", orderTemp.getOrderNo(), memberId, paymentPrice);
        return order.getOrderId();
    }

    /**
     * Webhook / 내부 호출용 — memberId 없이 orderNo 기반 confirmOrder
     */
    @Transactional
    public Long confirmOrderByOrderNo(String orderNo, String paymentKey) {
        OrderTemp orderTemp = orderTempRepository.findByOrderNoAndStatus(orderNo, "PENDING")
                .orElse(null);

        // 이미 처리된 경우 (idempotent)
        if (orderTemp == null) {
            OrderTemp completed = orderTempRepository.findByOrderNo(orderNo).orElse(null);
            if (completed != null && "COMPLETED".equals(completed.getStatus())) {
                log.info("이미 확정된 주문 - orderNo: {}", orderNo);
                return null;
            }
            throw new BusinessException("유효하지 않은 주문입니다.", ErrorCode.ENTITY_NOT_FOUND);
        }

        // memberId 조회
        Member member = memberRepository.findByMemberCode(orderTemp.getMemberCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        OrderDTO.ConfirmRequest confirmRequest = OrderDTO.ConfirmRequest.builder()
                .orderNo(orderNo)
                .paymentKey(paymentKey)
                .build();

        return confirmOrder(member.getMemberId(), confirmRequest);
    }

    // --- Private Helpers ---

    private void validateProductForOrder(Product product) {
        if ("Y".equals(product.getDeleteYn())) {
            throw new BusinessException("'" + product.getName() + "' 상품은 삭제된 상품입니다.", ErrorCode.ENTITY_NOT_FOUND);
        }
        if (!"Y".equals(product.getApplyYn())) {
            throw new BusinessException("'" + product.getName() + "' 상품은 현재 판매 승인되지 않은 상품입니다.", ErrorCode.INVALID_INPUT_VALUE);
        }
        if (product.getStatus() != ProductStatus.SALE) {
            throw new BusinessException("'" + product.getName() + "' 상품은 현재 판매 중이 아닙니다.", ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private int resolveBasePrice(Product product, ProductSite siteInfo, Member member) {
        int basePrice = product.getRetailPrice() != null ? product.getRetailPrice() : 0;
        if (siteInfo != null) {
            MemberRole role = member.getRole();
            if (role == MemberRole.ROLE_BIZ) {
                basePrice = siteInfo.getAPrice().intValue();
            } else if (role == MemberRole.ROLE_VETERAN) {
                basePrice = siteInfo.getCPrice().intValue();
            } else {
                basePrice = siteInfo.getBPrice().intValue();
            }
        }
        return basePrice;
    }

    private int resolveOptionExtraPrice(Product product, Long optionId) {
        if (optionId == null) return 0;
        ProductOption found = product.getOptions().stream()
                .filter(opt -> opt.getId().equals(optionId) && "Y".equals(opt.getUseYn()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "'" + product.getName() + "' 상품에 존재하지 않는 옵션입니다.",
                        ErrorCode.INVALID_INPUT_VALUE));
        return found.getExtraPrice();
    }

    private String resolveOptionName(Product product, Long optionId) {
        if (optionId == null) return null;
        ProductOption found = product.getOptions().stream()
                .filter(opt -> opt.getId().equals(optionId) && "Y".equals(opt.getUseYn()))
                .findFirst()
                .orElse(null);
        if (found == null) return null;
        return found.getName1() + (found.getName2() != null ? " " + found.getName2() : "");
    }

    private void validatePointUsage(Member member, int requestedPoint, BigDecimal totalPrice) {
        int currentBalance = pointRepository.calculateBalance(member.getMemberCode());
        if (currentBalance < requestedPoint) {
            throw new BusinessException("포인트 잔액이 부족합니다. (보유: " + currentBalance + "P, 요청: " + requestedPoint + "P)",
                    ErrorCode.INVALID_INPUT_VALUE);
        }
        com.nanum.domain.shop.model.ShopInfo shopInfo = shopInfoRepository.findBySiteCd(SITE_CD);
        if (shopInfo != null && shopInfo.getShopSetProductUseMaxPoint() != null
                && shopInfo.getShopSetProductUseMaxPoint().compareTo(BigDecimal.ZERO) > 0) {
            int maxUsablePoint = totalPrice.multiply(shopInfo.getShopSetProductUseMaxPoint())
                    .divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.FLOOR).intValue();
            if (requestedPoint > maxUsablePoint) {
                throw new BusinessException("포인트는 주문 금액의 " + shopInfo.getShopSetProductUseMaxPoint().intValue()
                        + "%까지만 사용 가능합니다. (최대: " + maxUsablePoint + "P)",
                        ErrorCode.INVALID_INPUT_VALUE);
            }
        }
    }

    private int calculateCouponDiscount(Member member, Long memberCouponId, BigDecimal totalPrice) {
        MemberCoupon memberCoupon = memberCouponRepository.findByIdAndMemberMemberCode(memberCouponId, member.getMemberCode())
                .orElseThrow(() -> new BusinessException("유효하지 않은 쿠폰입니다.", ErrorCode.ENTITY_NOT_FOUND));
        if ("Y".equals(memberCoupon.getUsedYn())) {
            throw new BusinessException("이미 사용된 쿠폰입니다.", ErrorCode.INVALID_INPUT_VALUE);
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(memberCoupon.getCoupon().getValidStartDate()) || now.isAfter(memberCoupon.getCoupon().getValidEndDate())) {
            throw new BusinessException("쿠폰 유효기간이 아닙니다.", ErrorCode.INVALID_INPUT_VALUE);
        }
        if (memberCoupon.getCoupon().getMinOrderPrice() != null
                && totalPrice.compareTo(BigDecimal.valueOf(memberCoupon.getCoupon().getMinOrderPrice())) < 0) {
            throw new BusinessException("최소 주문 금액(" + memberCoupon.getCoupon().getMinOrderPrice() + "원) 이상이어야 쿠폰을 사용할 수 있습니다.",
                    ErrorCode.INVALID_INPUT_VALUE);
        }
        int discount;
        if ("RATE".equals(memberCoupon.getCoupon().getDiscountType())) {
            discount = totalPrice.multiply(BigDecimal.valueOf(memberCoupon.getCoupon().getDiscountValue()))
                    .divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.FLOOR).intValue();
            if (memberCoupon.getCoupon().getMaxDiscount() != null && discount > memberCoupon.getCoupon().getMaxDiscount()) {
                discount = memberCoupon.getCoupon().getMaxDiscount();
            }
        } else {
            discount = memberCoupon.getCoupon().getDiscountValue();
        }
        return discount;
    }

    private OrderDTO.OrderDetailResponse toOrderDetailResponse(OrderDetail d) {
        BigDecimal unitPrice = d.getProductPrice().add(
                d.getOptionPrice() != null ? d.getOptionPrice() : BigDecimal.ZERO);
        return OrderDTO.OrderDetailResponse.builder()
                .orderDetailId(d.getId())
                .productId(d.getProductId())
                .productName(d.getProductName())
                .optionName(d.getOptionName())
                .quantity(d.getQuantity())
                .pricePerUnit(unitPrice)
                .totalPrice(d.getTotalPrice())
                .reviewYn("Y".equals(d.getReviewYn()))
                .build();
    }

    private PaymentMethod parsePaymentMethod(String method) {
        if (method == null || method.isBlank()) {
            throw new BusinessException("결제 수단을 선택해주세요.", ErrorCode.INVALID_INPUT_VALUE);
        }
        try {
            return PaymentMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("유효하지 않은 결제 수단입니다: " + method, ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void deleteCartItemsForOrder(Member member, List<Long> productIds) {
        try {
            var cartItems = cartRepository.findByMemberMemberCodeAndProduct_IdInAndDeleteYn(
                    member.getMemberCode(), productIds, "N");
            for (var cart : cartItems) {
                cart.delete(member.getMemberId());
            }
        } catch (Exception e) {
            log.warn("장바구니 항목 삭제 중 오류 발생 (주문은 정상 처리됨): {}", e.getMessage());
        }
    }
}
