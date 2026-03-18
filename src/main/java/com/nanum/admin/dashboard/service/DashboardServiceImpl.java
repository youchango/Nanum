package com.nanum.admin.dashboard.service;

import com.nanum.domain.claim.model.Claim;
import com.nanum.domain.claim.repository.ClaimRepository;
import com.nanum.admin.dashboard.dto.DashboardDTO;
import com.nanum.domain.delivery.model.Delivery;
import com.nanum.domain.inquiry.model.Inquiry;
import com.nanum.domain.inquiry.repository.InquiryRepository;
import com.nanum.domain.order.model.OrderMaster;
import com.nanum.domain.payment.model.PaymentMaster;
import com.nanum.domain.point.model.Point;
import com.nanum.user.delivery.repository.DeliveryRepository;
import com.nanum.user.order.repository.OrderRepository;
import com.nanum.user.payment.repository.PaymentRepository;
import com.nanum.user.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

        private final OrderRepository orderRepository;
        private final ClaimRepository claimRepository;
        private final PaymentRepository paymentRepository;
        private final PointRepository pointRepository;
        private final InquiryRepository inquiryRepository;
        private final DeliveryRepository deliveryRepository;

        @Override
        public DashboardDTO getDashboardSummary(String siteCd) {
                boolean isMaster = siteCd == null || siteCd.isEmpty();

                // 1. Orders
                List<OrderMaster> orders = isMaster ? orderRepository.findTop5ByOrderByCreatedAtDesc()
                                : orderRepository.findTop5BySiteCdOrderByCreatedAtDesc(siteCd);
                List<DashboardDTO.OrderSummary> orderSummaries = orders.stream()
                                .map(o -> DashboardDTO.OrderSummary.builder()
                                                .orderNo(o.getOrderId().toString()) // Assuming orderId is used as
                                                                                    // orderNo for now, or use
                                                                                    // o.getOrderName() if preferred
                                                .productName(o.getOrderName())
                                                .ordererName(o.getMember() != null ? o.getMember().getMemberName()
                                                                : "비회원")
                                                .orderStatus(o.getStatus().name())
                                                .orderDate(o.getCreatedAt())
                                                .build())
                                .toList();

                // 2. Claims
                List<Claim> claims = isMaster ? claimRepository.findTop5ByOrderByRequestedAtDesc()
                                : claimRepository.findTop5BySiteCdOrderByRequestedAtDesc(siteCd);
                List<DashboardDTO.ClaimSummary> claimSummaries = claims.stream()
                                .map(c -> DashboardDTO.ClaimSummary.builder()
                                                .claimId(c.getClaimId())
                                                .reason(c.getClaimReason())
                                                .status(c.getClaimStatus())
                                                .requestDate(c.getRequestedAt())
                                                .build())
                                .toList();

                // 3. Payments
                List<PaymentMaster> payments = isMaster ? paymentRepository.findTop5ByOrderByPaymentDateDesc()
                                : paymentRepository.findTop5ByOrderMaster_SiteCdOrderByPaymentDateDesc(siteCd);
                List<DashboardDTO.PaymentSummary> paymentSummaries = payments.stream()
                                .map(p -> DashboardDTO.PaymentSummary.builder()
                                                .paymentNo(p.getPaymentId().toString())
                                                .paymentMethod(p.getPaymentMethod().name())
                                                .finalPayPrice(p.getPaymentPrice() != null
                                                                ? p.getPaymentPrice().longValue()
                                                                : 0L)
                                                .paymentDate(p.getPaymentDate())
                                                .build())
                                .toList();

                // 4. Points
                List<Point> points = isMaster ? pointRepository.findTop5ByOrderByCreatedAtDesc()
                                : pointRepository.findTop5BySiteCdOrderByCreatedAtDesc(siteCd);
                List<DashboardDTO.PointSummary> pointSummaries = points.stream()
                                .map(p -> DashboardDTO.PointSummary.builder()
                                                .memberCode(p.getMember() != null ? p.getMember().getMemberCode() : "")
                                                .point(p.getPointUse() != null ? p.getPointUse().longValue() : 0L)
                                                .type(p.getPointGubun())
                                                .processDate(p.getCreatedAt())
                                                .build())
                                .toList();

                // 5. Inquiries
                List<Inquiry> inquiries = isMaster ? inquiryRepository.findTop5ByOrderByCreatedAtDesc()
                                : inquiryRepository.findTop5BySiteCdOrderByCreatedAtDesc(siteCd);
                List<DashboardDTO.InquirySummary> inquirySummaries = inquiries.stream()
                                .map(i -> DashboardDTO.InquirySummary.builder()
                                                .inquiryId(i.getId())
                                                .title(i.getTitle())
                                                .writerName(i.getWriter() != null ? i.getWriter().getMemberName()
                                                                : "알수없음")
                                                .answerYn(i.getStatus().name())
                                                .regDate(i.getCreatedAt())
                                                .build())
                                .toList();

                // 6. Deliveries (Master sees all, SCM sees their own siteCd)
                List<Delivery> deliveries = isMaster ? deliveryRepository.findTop5ByOrderByCreatedAtDesc()
                                : deliveryRepository.findTop5BySiteCdOrderByCreatedAtDesc(siteCd);
                List<DashboardDTO.DeliverySummary> deliverySummaries = deliveries.stream()
                                .map(d -> DashboardDTO.DeliverySummary.builder()
                                                .trackingNo(d.getTrackingNumber())
                                                .status(d.getStatus().name())
                                                .courier(d.getDeliveryCorp())
                                                .deliveryDate(d.getShippedAt() != null ? d.getShippedAt()
                                                                : d.getCreatedAt())
                                                .build())
                                .toList();

                return DashboardDTO.builder()
                                .recentOrders(orderSummaries)
                                .recentClaims(claimSummaries)
                                .recentPayments(paymentSummaries)
                                .recentPoints(pointSummaries)
                                .recentInquiries(inquirySummaries)
                                .recentDeliveries(deliverySummaries)
                                .build();
        }
}
