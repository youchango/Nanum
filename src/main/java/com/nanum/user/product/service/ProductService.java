package com.nanum.user.product.service;

import com.nanum.global.error.exception.BusinessException;
import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.domain.product.model.Product;
import com.nanum.domain.product.repository.ProductRepository;
import com.nanum.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

        private final ProductRepository productRepository;
        private final com.nanum.domain.product.repository.ProductSiteRepository productSiteRepository;
        private final com.nanum.user.member.repository.MemberRepository memberRepository;
        private final com.nanum.domain.file.service.FileService fileService;

        public List<ProductDTO.MallProductResponse> getMallProductList(com.nanum.global.common.dto.SearchDTO searchDTO,
                        String siteCd,
                        String memberCode) {
                if (siteCd == null) {
                        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
                }

                List<Product> products = productRepository.findMallProducts(siteCd, searchDTO);
                com.nanum.domain.member.model.Member member = memberCode != null
                                ? memberRepository.findByMemberCode(memberCode).orElse(null)
                                : null;

                return products.stream()
                                .map(p -> toMallResponse(p, siteCd, member, false))
                                .collect(Collectors.toList());
        }

        public java.util.Map<String, Object> getMallProductListWithCount(com.nanum.global.common.dto.SearchDTO searchDTO,
                        String siteCd,
                        String memberCode) {
                List<ProductDTO.MallProductResponse> list = getMallProductList(searchDTO, siteCd, memberCode);
                int totalCount = productRepository.countMallProducts(siteCd, searchDTO);

                java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
                result.put("content", list);
                result.put("page", searchDTO.getPage());
                result.put("recordSize", searchDTO.getRecordSize());
                result.put("totalCount", totalCount);
                result.put("totalPages", (int) Math.ceil((double) totalCount / searchDTO.getRecordSize()));
                return result;
        }

        public List<ProductDTO.MallProductResponse> getMainProductList(com.nanum.global.common.dto.SearchDTO searchDTO,
                        String siteCd,
                        String memberCode) {
                if (siteCd == null) {
                        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
                }

                List<Product> products = productRepository.findMainProducts(siteCd, searchDTO);
                com.nanum.domain.member.model.Member member = memberCode != null
                                ? memberRepository.findByMemberCode(memberCode).orElse(null)
                                : null;

                return products.stream()
                                .map(p -> toMallResponse(p, siteCd, member, false))
                                .collect(Collectors.toList());
        }

        public ProductDTO.MallProductResponse getMallProduct(Long productId, String siteCd, String memberCode) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

                if ("Y".equals(product.getDeleteYn()) || !"Y".equals(product.getApplyYn())) {
                        throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
                }

                com.nanum.domain.member.model.Member member = memberCode != null
                                ? memberRepository.findByMemberCode(memberCode).orElse(null)
                                : null;

                return toMallResponse(product, siteCd, member, true); // 상세 조회 시 모든 이미지 포함
        }

        /**
         * 상품 정보를 쇼핑몰용 응답 DTO로 변환합니다.
         * 
         * @param product             상품 엔티티
         * @param siteCd              사이트 코드
         * @param member              회원 정보 (로그인 여부에 따른 가격 계산용)
         * @param includeDetailImages 상세 이미지 포함 여부 (상세 조회 시 true, 리스트 조회 시 false)
         * @return 쇼핑몰용 상품 응답 DTO
         */
        private ProductDTO.MallProductResponse toMallResponse(Product product, String siteCd,
                        com.nanum.domain.member.model.Member member, boolean includeDetailImages) {
                // 1. 해당 사이트의 상품 가격 정보 조회
                com.nanum.domain.product.model.ProductSite productSite = productSiteRepository
                                .findByProductAndSiteCd(product, siteCd)
                                .stream().findFirst()
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

                if (!"Y".equals(productSite.getViewYn()) || "Y".equals(productSite.getDeleteYn())) {
                        throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
                }

                // 2. 회원 등급별 가격 결정
                int price = getBasePriceByMemberType(productSite, member);

                // 3. 이미지 정보 처리
                List<ProductDTO.Image> images = fileService
                                .getFiles(com.nanum.domain.file.model.ReferenceType.PRODUCT,
                                                String.valueOf(product.getId()))
                                .stream()
                                .filter(f -> includeDetailImages || "Y".equals(f.getIsMain())) // 리스트 조회 시 MAIN만, 상세 시
                                                                                               // 전체
                                .map(f -> ProductDTO.Image.builder()
                                                .fileId(f.getFileId())
                                                .imageUrl(fileService.getFullUrl(f.getPath())) // Full URL 변환
                                                .type("Y".equals(f.getIsMain()) ? "MAIN" : "DETAIL") // 타입 구분 명확화
                                                .displayOrder(f.getDisplayOrder())
                                                .build())
                                .collect(Collectors.toList());

                // 4. 옵션 정보 (ProductOption에서 직접 조회)
                List<ProductDTO.MallOptionResponse> options = product.getOptions().stream()
                                .filter(opt -> "Y".equals(opt.getUseYn()))
                                .map(opt -> {
                                        return ProductDTO.MallOptionResponse.builder()
                                                        .optionId(opt.getId())
                                                        .title1(opt.getTitle1())
                                                        .name1(opt.getName1())
                                                        .title2(opt.getTitle2())
                                                        .name2(opt.getName2())
                                                        .title3(opt.getTitle3())
                                                        .name3(opt.getName3())
                                                        .extraPrice(opt.getExtraPrice())
                                                        .stockQuantity(opt.getStockQuantity())
                                                        .build();
                                })
                                .collect(Collectors.toList());

                return ProductDTO.MallProductResponse.builder()
                                .productId(product.getId())
                                .categoryIds(product.getCategories().stream().map(c -> c.getCategoryId())
                                                .collect(Collectors.toList()))
                                .categoryName(product.getCategories().isEmpty() ? null
                                                : product.getCategories().get(0).getCategoryName())
                                .name(product.getName())
                                .brandName(product.getBrandName())
                                .supplyPrice(product.getSupplyPrice())
                                .mapPrice(product.getMapPrice())
                                .retailPrice(product.getRetailPrice())
                                .suggestedPrice(product.getSuggestedPrice())
                                .applyYn(product.getApplyYn())
                                .price(price)
                                .status(product.getStatus())
                                .optionYn(product.getOptionYn())
                                .description(product.getDescription())
                                .options(options)
                                .images(images)
                                .build();
        }

        private int getBasePriceByMemberType(com.nanum.domain.product.model.ProductSite ps,
                        com.nanum.domain.member.model.Member member) {
                if (member == null)
                        return ps.getBPrice().intValue(); // 비로그인 시 일반회원가

                com.nanum.domain.member.model.MemberType type = member.getMemberType();
                com.nanum.domain.member.model.MemberRole role = member.getRole();

                if (type == com.nanum.domain.member.model.MemberType.B
                                && role == com.nanum.domain.member.model.MemberRole.ROLE_BIZ) {
                        return ps.getAPrice().intValue();
                } else if (type == com.nanum.domain.member.model.MemberType.V
                                && role == com.nanum.domain.member.model.MemberRole.ROLE_VETERAN) {
                        return ps.getCPrice().intValue();
                } else {
                        return ps.getBPrice().intValue(); // 기본 일반회원가
                }
        }

        @org.springframework.transaction.annotation.Transactional
        public void increaseViewCount(Long productId) {
                productRepository.increaseViewCount(productId);
        }

        public List<ProductDTO.Response> getProductList(Long categoryId) {
                // Simple implementation - should be improved with QueryDSL for paging/search
                return productRepository.findAll().stream()
                                .filter(p -> "N".equals(p.getDeleteYn()))
                                .filter(p -> categoryId == null
                                                || p.getCategories().stream()
                                                                .anyMatch(c -> c.getCategoryId().equals(categoryId)))
                                .map(this::toResponse)
                                .collect(Collectors.toList());
        }

        public ProductDTO.Response getProduct(Long productId) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
                if ("Y".equals(product.getDeleteYn())) {
                        throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
                }
                return toResponse(product);
        }

        private ProductDTO.Response toResponse(Product product) {
                List<ProductDTO.Image> images = fileService
                                .getFiles(com.nanum.domain.file.model.ReferenceType.PRODUCT,
                                                String.valueOf(product.getId()))
                                .stream()
                                .map(f -> ProductDTO.Image.builder()
                                                .fileId(f.getFileId())
                                                .imageUrl(fileService.getFullUrl(f.getPath())) // Full URL 변환
                                                .type("Y".equals(f.getIsMain()) ? "MAIN" : "DETAIL") // 타입 구분
                                                .displayOrder(f.getDisplayOrder())
                                                .build())
                                .collect(Collectors.toList());

                com.nanum.domain.product.model.ProductCategory firstCategory = product.getCategories().isEmpty() ? null
                                : product.getCategories().get(0);

                return ProductDTO.Response.builder()
                                .productId(product.getId())
                                .categoryId(product.getCategories().stream().map(c -> c.getCategoryId())
                                                .collect(Collectors.toList()))
                                .categoryName(firstCategory != null ? firstCategory.getCategoryName() : null)
                                .name(product.getName())
                                .brandName(product.getBrandName())
                                .mapPrice(product.getMapPrice())
                                .retailPrice(product.getRetailPrice())
                                .suggestedPrice(product.getSuggestedPrice())
                                .status(product.getStatus())
                                .applyYn(product.getApplyYn())
                                .images(images)
                                .build();
        }
}
