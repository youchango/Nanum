package com.nanum.user.payment.service;

import com.nanum.domain.member.model.Member;
import com.nanum.domain.order.model.OrderMaster;
import com.nanum.domain.payment.dto.TaxBillDTO;
import com.nanum.domain.payment.model.MemberTaxBillInfo;
import com.nanum.domain.payment.model.TaxBillApply;
import com.nanum.domain.payment.repository.MemberTaxBillInfoRepository;
import com.nanum.domain.payment.repository.TaxBillApplyRepository;
import com.nanum.user.member.repository.MemberRepository;
import com.nanum.user.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaxBillService {

    private final MemberTaxBillInfoRepository taxBillInfoRepository;
    private final TaxBillApplyRepository taxBillApplyRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    // ==================== 세금계산서 정보 관리 (여러 건) ====================

    /** 내 세금계산서 정보 목록 조회 */
    public List<TaxBillDTO.TaxInfoResponse> getTaxInfoList(String memberId) {
        Member member = findMember(memberId);
        return taxBillInfoRepository.findAllByMemberMemberCodeAndInfoTypeOrderByCreatedAtDesc(
                member.getMemberCode(), "TAX_BILL")
                .stream().map(this::toTaxInfoResponse).collect(Collectors.toList());
    }

    /** 세금계산서 정보 단건 조회 */
    public TaxBillDTO.TaxInfoResponse getTaxInfo(String memberId, Long id) {
        Member member = findMember(memberId);
        MemberTaxBillInfo info = taxBillInfoRepository.findByIdAndMemberMemberCode(id, member.getMemberCode())
                .orElseThrow(() -> new IllegalArgumentException("세금계산서 정보를 찾을 수 없습니다."));
        return toTaxInfoResponse(info);
    }

    /** 세금계산서 정보 등록 */
    @Transactional
    public Long createTaxInfo(String memberId, TaxBillDTO.TaxInfoRequest request) {
        Member member = findMember(memberId);

        MemberTaxBillInfo info = MemberTaxBillInfo.builder()
                .member(member)
                .infoType("TAX_BILL")
                .bizRegNum(request.getBizRegNum())
                .bizName(request.getBizName())
                .bizRepName(request.getBizRepName())
                .bizCategory(request.getBizCategory())
                .bizDetailCategory(request.getBizDetailCategory())
                .bizAddress(request.getBizAddress())
                .bizEmail(request.getBizEmail())
                .bizMobile(request.getBizMobile())
                .damName(request.getDamName())
                .build();

        return taxBillInfoRepository.save(info).getId();
    }

    /** 세금계산서 정보 수정 */
    @Transactional
    public void updateTaxInfo(String memberId, Long id, TaxBillDTO.TaxInfoRequest request) {
        Member member = findMember(memberId);
        MemberTaxBillInfo info = taxBillInfoRepository.findByIdAndMemberMemberCode(id, member.getMemberCode())
                .orElseThrow(() -> new IllegalArgumentException("세금계산서 정보를 찾을 수 없습니다."));

        info.update(
                request.getBizRegNum(), request.getBizName(), request.getBizRepName(),
                request.getBizCategory(), request.getBizDetailCategory(), request.getBizAddress(),
                request.getBizEmail(), request.getBizMobile(), request.getDamName(),
                null, null
        );
    }

    /** 세금계산서 정보 삭제 */
    @Transactional
    public void deleteTaxInfo(String memberId, Long id) {
        Member member = findMember(memberId);
        MemberTaxBillInfo info = taxBillInfoRepository.findByIdAndMemberMemberCode(id, member.getMemberCode())
                .orElseThrow(() -> new IllegalArgumentException("세금계산서 정보를 찾을 수 없습니다."));
        taxBillInfoRepository.delete(info);
    }

    // ==================== 발행 신청 ====================

    /** 세금계산서/현금영수증 발행 신청 */
    @Transactional
    public Long applyTaxBill(String memberId, TaxBillDTO.ApplyRequest request) {
        Member member = findMember(memberId);

        OrderMaster order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getMember().getMemberCode().equals(member.getMemberCode())) {
            throw new IllegalArgumentException("본인의 주문만 신청할 수 있습니다.");
        }

        // 중복 신청 방지
        taxBillApplyRepository
                .findByOrderIdAndMemberMemberCode(request.getOrderId(), member.getMemberCode())
                .ifPresent(existing -> {
                    if (!"CANCELLED".equals(existing.getStatus()) && !"FAILED".equals(existing.getStatus())) {
                        throw new IllegalArgumentException("이미 해당 주문에 대한 신청이 존재합니다.");
                    }
                });

        TaxBillApply apply = TaxBillApply.builder()
                .siteCd("NANUM")
                .member(member)
                .orderNo(order.getOrderNo())
                .orderId(order.getOrderId())
                .billType(request.getBillType())
                .bizRegNum(request.getBizRegNum())
                .bizName(request.getBizName())
                .bizRepName(request.getBizRepName())
                .bizCategory(request.getBizCategory())
                .bizDetailCategory(request.getBizDetailCategory())
                .bizAddress(request.getBizAddress())
                .bizEmail(request.getBizEmail())
                .bizMobile(request.getBizMobile())
                .receiptPurpose(request.getReceiptPurpose())
                .receiptIdNum(request.getReceiptIdNum())
                .build();

        return taxBillApplyRepository.save(apply).getId();
    }

    /** 내 신청 목록 조회 */
    public Page<TaxBillDTO.ApplyResponse> getMyApplyList(String memberId, Pageable pageable) {
        Member member = findMember(memberId);
        return taxBillApplyRepository
                .findByMemberMemberCodeOrderByCreatedAtDesc(member.getMemberCode(), pageable)
                .map(this::toApplyResponse);
    }

    /** 신청 상세 조회 */
    public TaxBillDTO.ApplyResponse getApplyDetail(String memberId, Long applyId) {
        Member member = findMember(memberId);
        TaxBillApply apply = taxBillApplyRepository
                .findByIdAndMemberMemberCode(applyId, member.getMemberCode())
                .orElseThrow(() -> new IllegalArgumentException("신청 내역을 찾을 수 없습니다."));
        return toApplyResponse(apply);
    }

    // ==================== Private Helpers ====================

    private Member findMember(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }

    private TaxBillDTO.TaxInfoResponse toTaxInfoResponse(MemberTaxBillInfo info) {
        return TaxBillDTO.TaxInfoResponse.builder()
                .id(info.getId())
                .infoType(info.getInfoType())
                .bizRegNum(info.getBizRegNum())
                .bizName(info.getBizName())
                .bizRepName(info.getBizRepName())
                .bizCategory(info.getBizCategory())
                .bizDetailCategory(info.getBizDetailCategory())
                .bizAddress(info.getBizAddress())
                .bizEmail(info.getBizEmail())
                .bizMobile(info.getBizMobile())
                .damName(info.getDamName())
                .build();
    }

    private TaxBillDTO.ApplyResponse toApplyResponse(TaxBillApply apply) {
        return TaxBillDTO.ApplyResponse.builder()
                .id(apply.getId())
                .orderNo(apply.getOrderNo())
                .orderId(apply.getOrderId())
                .billType(apply.getBillType())
                .status(apply.getStatus())
                .bizRegNum(apply.getBizRegNum())
                .bizName(apply.getBizName())
                .receiptIdNum(apply.getReceiptIdNum())
                .issueDate(apply.getIssueDate())
                .ntsConfirmNum(apply.getNtsConfirmNum())
                .createdAt(apply.getCreatedAt())
                .build();
    }
}
