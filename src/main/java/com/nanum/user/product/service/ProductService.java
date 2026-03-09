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
        private final com.nanum.domain.product.repository.ProductOptionSiteRepository productOptionSiteRepository;
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
                                .map(p -> toMallResponse(p, siteCd, member))
                                .collect(Collectors.toList());
        }

        public ProductDTO.MallProductResponse getMallProduct(Long productId, String siteCd, String memberCode) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

                if ("Y".equals(product.getDeleteYn())) {
                        throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
                }

                com.nanum.domain.member.model.Member member = memberCode != null
                                ? memberRepository.findByMemberCode(memberCode).orElse(null)
                                : null;

                return toMallResponse(product, siteCd, member);
        }

        private ProductDTO.MallProductResponse toMallResponse(Product product, String siteCd,
                        com.nanum.domain.member.model.Member member) {
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

                // 3. 이미지 정보
                List<ProductDTO.Image> images = fileService
                                .getFiles(com.nanum.domain.file.model.ReferenceType.PRODUCT,
                                                String.valueOf(product.getId()))
                                .stream()
                                .map(f -> ProductDTO.Image.builder()
                                                .fileId(f.getFileId())
                                                .imageUrl(f.getPath())
                                                .type(f.getReferenceType().name())
                                                .displayOrder(f.getDisplayOrder())
                                                .build())
                                .collect(Collectors.toList());

                // 4. 옵션 정보 및 등급별 추가금 적용
                List<com.nanum.domain.product.model.ProductOptionSite> optionSites = productOptionSiteRepository
                                .findByProductSitePsId(productSite.getPsId());

                List<ProductDTO.MallOptionResponse> options = optionSites.stream()
                                .map(os -> {
                                        com.nanum.domain.product.model.ProductOption opt = os.getProductOption();
                                        return ProductDTO.MallOptionResponse.builder()
                                                        .optionId(opt.getId())
                                                        .title1(opt.getTitle1())
                                                        .name1(opt.getName1())
                                                        .title2(opt.getTitle2())
                                                        .name2(opt.getName2())
                                                        .title3(opt.getTitle3())
                                                        .name3(opt.getName3())
                                                        .extraPrice(getExtraPriceByMemberType(os, member))
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
                                .supplyPrice(product.getSupplyPrice())
                                .mapPrice(product.getMapPrice())
                                .standardPrice(product.getStandardPrice())
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

        private int getExtraPriceByMemberType(com.nanum.domain.product.model.ProductOptionSite os,
                        com.nanum.domain.member.model.Member member) {
                if (member == null)
                        return os.getBExtraPrice().intValue();

                com.nanum.domain.member.model.MemberType type = member.getMemberType();
                com.nanum.domain.member.model.MemberRole role = member.getRole();

                if (type == com.nanum.domain.member.model.MemberType.B
                                && role == com.nanum.domain.member.model.MemberRole.ROLE_BIZ) {
                        return os.getAExtraPrice().intValue();
                } else if (type == com.nanum.domain.member.model.MemberType.V
                                && role == com.nanum.domain.member.model.MemberRole.ROLE_VETERAN) {
                        return os.getCExtraPrice().intValue();
                } else {
                        return os.getBExtraPrice().intValue();
                }
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
                                                .imageUrl(f.getPath())
                                                .type(f.getReferenceType().name())
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
                                .mapPrice(product.getMapPrice())
                                .standardPrice(product.getStandardPrice())
                                .status(product.getStatus())
                                .images(images)
                                .build();
        }
}
