package com.nanum.admin.management.service;

import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.entity.ManagerType;
import com.nanum.admin.manager.service.CustomManagerDetails;
import com.nanum.domain.inquiry.dto.InquiryDTO;
import com.nanum.domain.inquiry.model.Inquiry;
import com.nanum.domain.inquiry.repository.InquiryRepository;
import com.nanum.domain.product.repository.ProductRepository;
import com.nanum.user.order.repository.OrderRepository;
import com.nanum.domain.shop.repository.ShopInfoRepository;
import com.nanum.domain.shop.model.ShopInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminInquiryService {

    private final InquiryRepository inquiryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ShopInfoRepository shopInfoRepository;

    public Page<InquiryDTO.Response> getInquiries(InquiryDTO.Search search, Pageable pageable) {
        Manager manager = getCurrentManager();
        // MASTER 또는 SCM 권한이 아니면 자신의 사이트 코드 강제 설정
        if (manager.getMbType() != ManagerType.MASTER && manager.getMbType() != ManagerType.SCM) {
            search.setSiteCd(manager.getSiteCd());
        }

        return inquiryRepository.search(search, pageable);
    }

    public InquiryDTO.Response getInquiry(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다."));
        InquiryDTO.Response response = InquiryDTO.Response.from(inquiry);
        
        // 상세에서도 상품/주문명 보정을 위해 enrichResponse 사용 (목록은 리포지토리 조인으로 해결)
        if (response.getProductId() != null) {
            productRepository.findById(response.getProductId())
                    .ifPresent(product -> response.setProductName(product.getName()));
        }
        if (response.getOrderNo() != null && !response.getOrderNo().isEmpty()) {
            orderRepository.findByOrderNo(response.getOrderNo())
                    .ifPresent(order -> response.setOrderName(order.getOrderName()));
        }
        if (response.getSiteCd() != null) {
            ShopInfo shop = shopInfoRepository.findBySiteCd(response.getSiteCd());
            if (shop != null) {
                response.setShopName(shop.getShopName());
            }
        }
        return response;
    }

    @Transactional
    public void replyInquiry(Long id, InquiryDTO.ReplyRequest request) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다."));

        Manager manager = getCurrentManager();

        inquiry.reply(request.getAnswer(), manager);
    }

    private Manager getCurrentManager() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomManagerDetails) {
            return ((CustomManagerDetails) principal).getManager();
        }
        throw new IllegalStateException("인증된 관리자 정보가 없습니다.");
    }
}
