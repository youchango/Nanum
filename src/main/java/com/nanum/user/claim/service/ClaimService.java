package com.nanum.user.claim.service;

import com.nanum.domain.claim.dto.ClaimDTO;
import com.nanum.domain.claim.model.Claim;
import com.nanum.domain.claim.repository.ClaimRepository;
import com.nanum.domain.member.model.Member;
import com.nanum.domain.order.model.OrderDetail;
import com.nanum.domain.order.model.OrderMaster;
import com.nanum.domain.order.model.OrderStatus;
import com.nanum.global.error.ErrorCode;
import com.nanum.global.error.exception.BusinessException;
import com.nanum.user.member.repository.MemberRepository;
import com.nanum.user.order.repository.OrderDetailRepository;
import com.nanum.user.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userClaimService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    private static final List<String> VALID_CLAIM_TYPES = List.of("EXCHANGE", "RETURN", "REFUND");
    private static final List<String> VALID_CLAIM_REASONS = List.of(
            "ORDER_ERROR", "CHANGE_OF_MIND", "DEFECTIVE", "DAMAGED", "MISDELIVERY", "OTHER"
    );

    /**
     * 클레임(교환/반품/환불) 접수
     */
    @Transactional
    public Long createClaim(String memberId, ClaimDTO.CreateRequest request) {
        // 1. 회원 조회
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 주문 조회 및 본인 소유 확인
        OrderMaster order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException("주문을 찾을 수 없습니다.", ErrorCode.ENTITY_NOT_FOUND));

        if (!order.getMember().getMemberCode().equals(member.getMemberCode())) {
            throw new BusinessException("본인의 주문만 클레임 접수할 수 있습니다.", ErrorCode.ACCESS_DENIED);
        }

        // 3. 배송완료 상태만 클레임 가능
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException("배송 완료된 주문만 교환/반품/환불 접수가 가능합니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        // 4. 클레임 유형 검증
        if (!VALID_CLAIM_TYPES.contains(request.getClaimType())) {
            throw new BusinessException("유효하지 않은 클레임 유형입니다: " + request.getClaimType(), ErrorCode.INVALID_INPUT_VALUE);
        }

        // 5. 클레임 사유 검증
        if (!VALID_CLAIM_REASONS.contains(request.getClaimReason())) {
            throw new BusinessException("유효하지 않은 클레임 사유입니다: " + request.getClaimReason(), ErrorCode.INVALID_INPUT_VALUE);
        }

        // 6. 주문상세별 중복 클레임 방지
        if (request.getOrderDetailId() != null) {
            boolean duplicateExists = claimRepository.existsByOrderDetailIdAndClaimStatusNot(
                    request.getOrderDetailId(), "REJECTED");
            if (duplicateExists) {
                throw new BusinessException("해당 주문 상품에 대해 이미 접수된 클레임이 있습니다.", ErrorCode.INVALID_INPUT_VALUE);
            }
        }

        // 7. 주문상세 정보로 상품 정보 세팅
        String productName = null;
        Long productId = null;
        if (request.getOrderDetailId() != null) {
            OrderDetail orderDetail = orderDetailRepository.findById(request.getOrderDetailId())
                    .orElseThrow(() -> new BusinessException("주문 상세를 찾을 수 없습니다.", ErrorCode.ENTITY_NOT_FOUND));

            if (!orderDetail.getOrderId().equals(order.getOrderId())) {
                throw new BusinessException("해당 주문에 속하지 않는 주문 상세입니다.", ErrorCode.INVALID_INPUT_VALUE);
            }

            productId = orderDetail.getProductId();
            productName = orderDetail.getProductName();
        }

        // 8. Claim 엔티티 생성 및 저장
        Claim claim = Claim.builder()
                .orderMaster(order)
                .member(member)
                .siteCd(order.getSiteCd())
                .claimType(request.getClaimType())
                .claimStatus("REQUESTED")
                .claimReason(request.getClaimReason())
                .claimReasonDetail(request.getClaimReasonDetail())
                .orderDetailId(request.getOrderDetailId())
                .productId(productId)
                .productName(productName)
                .quantity(request.getQuantity())
                .refundBankName(request.getRefundBankName())
                .refundAccountNum(request.getRefundAccountNum())
                .refundAccountName(request.getRefundAccountName())
                .build();

        claimRepository.save(claim);
        return claim.getClaimId();
    }

    /**
     * 내 클레임 목록 조회
     */
    public Page<ClaimDTO.Response> getMyClaims(String memberId, Pageable pageable) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Page<Claim> claims = claimRepository.findByMemberMemberCodeOrderByRequestedAtDesc(
                member.getMemberCode(), pageable);

        return claims.map(ClaimDTO.Response::from);
    }

    /**
     * 클레임 상세 조회 (본인 확인)
     */
    public ClaimDTO.Response getClaimDetail(String memberId, Long claimId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Claim claim = claimRepository.findByClaimIdAndMemberMemberCode(claimId, member.getMemberCode())
                .orElseThrow(() -> new BusinessException("클레임을 찾을 수 없습니다.", ErrorCode.ENTITY_NOT_FOUND));

        ClaimDTO.Response response = ClaimDTO.Response.from(claim);

        // 주문 전체 클레임일 때 상품 목록 포함
        if (claim.getOrderDetailId() == null) {
            java.util.List<OrderDetail> details = orderDetailRepository.findByOrderId(claim.getOrderMaster().getOrderId());
            response.setOrderItems(details.stream().map(d -> ClaimDTO.OrderItem.builder()
                    .productName(d.getProductName())
                    .optionName(d.getOptionName())
                    .quantity(d.getQuantity())
                    .pricePerUnit(d.getProductPrice().intValue() + (d.getOptionPrice() != null ? d.getOptionPrice().intValue() : 0))
                    .build()).toList());
        }

        return response;
    }
}
