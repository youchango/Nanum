package com.nanum.admin.product.service;

import com.nanum.global.error.exception.BusinessException;
import com.nanum.global.error.ErrorCode;
import com.nanum.admin.inout.service.AdminInoutService;

import com.nanum.domain.product.dto.AdminProductListDTO;
import com.nanum.domain.product.dto.AdminProductSearchDTO;
import com.nanum.domain.product.dto.ProductDTO;
import com.nanum.domain.product.dto.ProductSitePriceUpdateDTO;
import com.nanum.domain.product.dto.ProductSiteBulkCreateDTO;
import com.nanum.domain.product.model.*;
import com.nanum.domain.product.repository.ProductCategoryRepository;
import com.nanum.domain.product.repository.ProductOptionRepository;
import com.nanum.domain.product.repository.ProductRepository;
import com.nanum.domain.product.repository.ProductSiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.service.CustomManagerDetails;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductService {

        private final ProductRepository productRepository;
        private final ProductSiteRepository productSiteRepository; // Injected
        private final ProductCategoryRepository productCategoryRepository;
        private final ProductOptionRepository productOptionRepository;
        private final AdminInoutService adminInoutService;
        private final com.nanum.domain.file.service.FileService fileService;
        private final com.nanum.domain.shipment.repository.ShipmentRepository shipmentRepository;

        public Map<String, Object> getProducts(AdminProductSearchDTO searchDTO) {
                Manager manager = getCurrentManager();
                if ("MASTER".equals(manager.getMbType())) {
                } else {
                        searchDTO.setSiteCd(manager.getSiteCd());
                }

                int totalCount = productRepository.countAdminProducts(searchDTO);
                searchDTO.setPagination(totalCount);

                List<AdminProductListDTO> productList = productRepository.findAdminProducts(searchDTO);

                // 대표 이미지(MAIN) 일괄 조회 및 설정
                if (!productList.isEmpty()) {
                        List<String> productIds = productList.stream()
                                        .map(p -> String.valueOf(p.getId()))
                                        .collect(Collectors.toList());

                        Map<String, String> mainImageMap = fileService
                                        .getFiles(com.nanum.domain.file.model.ReferenceType.PRODUCT, productIds)
                                        .stream()
                                        .filter(f -> "Y".equals(f.getIsMain()))
                                        .collect(Collectors.toMap(
                                                        com.nanum.domain.file.model.FileStore::getReferenceId,
                                                        f -> fileService.getFullUrl(f.getPath()),
                                                        (existing, replacement) -> existing // 중복 시 기존값 유지
                                        ));

                        productList.forEach(p -> p.setThumbnailUrl(mainImageMap.get(String.valueOf(p.getId()))));
                }

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("productList", productList);
                responseData.put("totalCount", totalCount);

                return responseData;
        }

        public ProductDTO.Response getProduct(Long id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + id));

                List<com.nanum.domain.file.dto.FileResponseDTO> files = fileService
                                .getFiles(com.nanum.domain.file.model.ReferenceType.PRODUCT, String.valueOf(id))
                                .stream()
                                .map(com.nanum.domain.file.dto.FileResponseDTO::from)
                                .collect(Collectors.toList());

                ProductCategory firstCategory = product.getCategories().isEmpty() ? null
                                : product.getCategories().get(0);

                List<ProductDTO.Image> images = files.stream()
                                .map(f -> ProductDTO.Image.builder()
                                                .fileId(f.getFileId())
                                                .imageUrl(fileService.getFullUrl(f.getPath()))
                                                .type("Y".equals(f.getIsMain()) ? "MAIN" : "DETAIL")
                                                .displayOrder(f.getDisplayOrder())
                                                .build())
                                .collect(Collectors.toList());

                List<ProductDTO.Option> options = product.getOptions().stream()
                                .map(opt -> ProductDTO.Option.builder()
                                                .optionId(opt.getId())
                                                .title1(opt.getTitle1()).name1(opt.getName1())
                                                .title2(opt.getTitle2()).name2(opt.getName2())
                                                .title3(opt.getTitle3()).name3(opt.getName3())
                                                .extraPrice(opt.getExtraPrice())
                                                .stockQuantity(opt.getStockQuantity())
                                                .build())
                                .collect(Collectors.toList());

                return ProductDTO.Response.builder()
                                .productId(product.getId())
                                .categoryId(product.getCategories().stream().map(ProductCategory::getCategoryId)
                                                .collect(Collectors.toList()))
                                .categoryName(firstCategory != null ? firstCategory.getCategoryName() : null)
                                .name(product.getName())
                                .brandName(product.getBrandName())
                                .supplyPrice(product.getSupplyPrice())
                                .mapPrice(product.getMapPrice())
                                .retailPrice(product.getRetailPrice())
                                .suggestedPrice(product.getSuggestedPrice())
                                .safetyStock(product.getSafetyStock())
                                .stockQuantity(product.getStockQuantity())
                                .status(product.getStatus())
                                .applyYn(product.getApplyYn())
                                .optionYn(product.getOptionYn())
                                .description(product.getDescription())
                                // 신규 정책 필드 매핑
                                .reviewYn(product.getReviewYn())
                                .deliveryWay(product.getDeliveryWay())
                                .deliveryArea(product.getDeliveryArea())
                                .deliveryType(product.getDeliveryType())
                                .bundleShippingYn(product.getBundleShippingYn())
                                .deliveryPolicyType(product.getDeliveryPolicyType())
                                .deliveryMinOrderFee(product.getDeliveryMinOrderFee())
                                .outboundShipmentCode(product.getOutboundShipmentCode())
                                .inboundShipmentCode(product.getInboundShipmentCode())
                                .deliveryFee(product.getDeliveryFee())
                                .returnFee(product.getReturnFee())
                                .exchangeFee(product.getExchangeFee())
                                .deliveryIslandYn(product.getDeliveryIslandYn())
                                .deliveryIslandFee(product.getDeliveryIslandFee())
                                .outboundShipment(product.getOutboundShipmentCode() != null ? shipmentRepository
                                                .findByShipmentCode(product.getOutboundShipmentCode())
                                                .map(com.nanum.domain.shipment.dto.ShipmentDTO.Response::from)
                                                .orElse(null) : null)
                                .inboundShipment(product.getInboundShipmentCode() != null ? shipmentRepository
                                                .findByShipmentCode(product.getInboundShipmentCode())
                                                .map(com.nanum.domain.shipment.dto.ShipmentDTO.Response::from)
                                                .orElse(null) : null)
                                .options(options)
                                .images(images)
                                .build();
        }

        @Transactional
        public Long createProduct(ProductDTO.Request request) {
                if (request.getCategoryIds() == null || request.getCategoryIds().isEmpty()) {
                        throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
                }
                List<ProductCategory> categories = productCategoryRepository.findAllById(request.getCategoryIds());
                if (categories.isEmpty()) {
                        throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
                }

                Product product = Product.builder()
                                .categories(categories)
                                .name(request.getName())
                                .brandName(request.getBrandName())
                                .mapPrice(request.getMapPrice())
                                .retailPrice(request.getRetailPrice())
                                .suggestedPrice(request.getSuggestedPrice())
                                .safetyStock(request.getSafetyStock() != null ? request.getSafetyStock() : 0)
                                .optionYn(request.getOptionYn() != null ? request.getOptionYn() : "N")
                                .status(request.getStatus())
                                .applyYn("N") // 생성 시 무조건 N
                                .description(request.getDescription())
                                .supplyPrice(request.getSupplyPrice())
                                .viewCount(0)
                                // 신규 정책 필드 설정
                                .reviewYn(request.getReviewYn() != null ? request.getReviewYn() : "Y")
                                .deliveryWay(request.getDeliveryWay())
                                .deliveryArea(request.getDeliveryArea())
                                .deliveryType(request.getDeliveryType())
                                .bundleShippingYn(request.getBundleShippingYn() != null ? request.getBundleShippingYn()
                                                : "Y")
                                .deliveryPolicyType(request.getDeliveryPolicyType() != null
                                                ? request.getDeliveryPolicyType()
                                                : "MAX")
                                .deliveryMinOrderFee(request.getDeliveryMinOrderFee() != null
                                                ? request.getDeliveryMinOrderFee()
                                                : BigDecimal.ZERO)
                                .outboundShipmentCode(request.getOutboundShipmentCode())
                                .inboundShipmentCode(request.getInboundShipmentCode())
                                .deliveryFee(request.getDeliveryFee() != null ? request.getDeliveryFee()
                                                : BigDecimal.ZERO)
                                .returnFee(request.getReturnFee() != null ? request.getReturnFee() : BigDecimal.ZERO)
                                .exchangeFee(request.getExchangeFee() != null ? request.getExchangeFee()
                                                : BigDecimal.ZERO)
                                .deliveryIslandYn(request.getDeliveryIslandYn() != null ? request.getDeliveryIslandYn()
                                                : "Y")
                                .deliveryIslandFee(
                                                request.getDeliveryIslandFee() != null ? request.getDeliveryIslandFee()
                                                                : BigDecimal.ZERO)
                                .build();

                // Check if options usage is Y and add them
                if ("Y".equals(request.getOptionYn()) && request.getOptions() != null) {
                        List<ProductOption> options = request.getOptions().stream()
                                        .map(opt -> ProductOption.builder()
                                                        .product(product)
                                                        .title1(opt.getTitle1()).name1(opt.getName1())
                                                        .title2(opt.getTitle2()).name2(opt.getName2())
                                                        .title3(opt.getTitle3()).name3(opt.getName3())
                                                        .extraPrice(opt.getExtraPrice())
                                                        .stockQuantity(0) // 무조건 재고 0으로 등록 제한
                                                        .useYn("Y")
                                                        .build())
                                        .collect(Collectors.toList());
                        product.getOptions().addAll(options);
                }

                productRepository.save(product);
                productRepository.flush(); // Ensure IDs are generated for product and options

                // 2. Save ProductSite 로직 제거 (외부에서 처리하도록 변경)

                // 3. Link Files
                processFiles(request.getImages(), product.getId());

                // 4. Initialize Stock
                adminInoutService.initializeStock(product, product.getOptions());

                return product.getId();
        }

        @Transactional
        public void updateProduct(Long id, ProductDTO.Request request) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

                List<ProductCategory> categories = productCategoryRepository.findAllById(request.getCategoryIds());
                if (categories.isEmpty() || categories.size() != request.getCategoryIds().size()) {
                        throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
                }

                // Basic Info Update
                product.updateInfo(
                                categories,
                                request.getName(),
                                request.getBrandName(),
                                request.getSupplyPrice(),
                                request.getMapPrice(),
                                request.getRetailPrice(),
                                request.getSuggestedPrice(),
                                request.getSafetyStock() != null ? request.getSafetyStock() : 0,
                                request.getOptionYn() != null ? request.getOptionYn() : "N",
                                request.getStatus(),
                                request.getDescription(),
                                "N",
                                // 신규 정책 필드 업데이트
                                request.getReviewYn() != null ? request.getReviewYn() : "Y",
                                request.getDeliveryWay(),
                                request.getDeliveryArea(),
                                request.getDeliveryType(),
                                request.getBundleShippingYn() != null ? request.getBundleShippingYn() : "Y",
                                request.getDeliveryPolicyType() != null ? request.getDeliveryPolicyType() : "MAX",
                                request.getDeliveryMinOrderFee() != null ? request.getDeliveryMinOrderFee()
                                                : BigDecimal.ZERO,
                                request.getOutboundShipmentCode(),
                                request.getInboundShipmentCode(),
                                request.getDeliveryFee() != null ? request.getDeliveryFee() : BigDecimal.ZERO,
                                request.getReturnFee() != null ? request.getReturnFee() : BigDecimal.ZERO,
                                request.getExchangeFee() != null ? request.getExchangeFee() : BigDecimal.ZERO,
                                request.getDeliveryIslandYn() != null ? request.getDeliveryIslandYn() : "Y",
                                request.getDeliveryIslandFee() != null ? request.getDeliveryIslandFee()
                                                : BigDecimal.ZERO); // 수정 시 무조건 N

                // salePrice 변경 시 사이트별 가격 일괄 동기화 로직 제거

                // Options Update - 기존 옵션 병합 (추가, 수정, 삭제)
                List<ProductOption> existingOptions = product.getOptions();
                List<ProductDTO.Option> requestOptions = request.getOptions();

                if ("Y".equals(request.getOptionYn()) && requestOptions != null && !requestOptions.isEmpty()) {
                        List<Long> requestOptionIds = requestOptions.stream()
                                        .map(ProductDTO.Option::getOptionId)
                                        .filter(optId -> optId != null)
                                        .collect(Collectors.toList());

                        // 1. 삭제 대상 찾아서 제거 (외래키 제약조건 방지를 위해 자식 먼저 삭제)
                        List<ProductOption> optionsToRemove = existingOptions.stream()
                                        .filter(opt -> !requestOptionIds.contains(opt.getId()))
                                        .collect(Collectors.toList());

                        productOptionRepository.deleteAll(optionsToRemove);
                        for (ProductOption optToRemove : optionsToRemove) {
                                existingOptions.remove(optToRemove);
                        }

                        // 2. 수정 및 신규 추가
                        List<ProductOption> newlyAddedOptions = new ArrayList<>();
                        for (ProductDTO.Option reqOpt : requestOptions) {
                                if (reqOpt.getOptionId() != null) {
                                        existingOptions.stream()
                                                        .filter(opt -> opt.getId().equals(reqOpt.getOptionId()))
                                                        .findFirst()
                                                        .ifPresent(opt -> {
                                                                opt.updateBasicInfo(
                                                                                reqOpt.getTitle1(), reqOpt.getName1(),
                                                                                reqOpt.getTitle2(), reqOpt.getName2(),
                                                                                reqOpt.getTitle3(), reqOpt.getName3(),
                                                                                reqOpt.getExtraPrice());
                                                        });
                                } else {
                                        ProductOption newOpt = ProductOption.builder()
                                                        .product(product)
                                                        .title1(reqOpt.getTitle1()).name1(reqOpt.getName1())
                                                        .title2(reqOpt.getTitle2()).name2(reqOpt.getName2())
                                                        .title3(reqOpt.getTitle3()).name3(reqOpt.getName3())
                                                        .extraPrice(reqOpt.getExtraPrice())
                                                        .stockQuantity(reqOpt.getStockQuantity())
                                                        .useYn("Y")
                                                        .build();
                                        existingOptions.add(newOpt);
                                        newlyAddedOptions.add(newOpt);
                                }
                        }

                        // 신규 옵션들의 ID 발급을 위해 Flush
                        if (!newlyAddedOptions.isEmpty()) {
                                productRepository.saveAndFlush(product);
                        }
                } else {
                        // 옵션 사용 안함 처리 시 모든 옵션 데이터 삭제
                        for (ProductOption optToRemove : new ArrayList<>(existingOptions)) {
                                existingOptions.remove(optToRemove);
                        }
                }

                // Files Update
                processFiles(request.getImages(), id);

                productRepository.saveAndFlush(product);

                adminInoutService.initializeStock(product, product.getOptions());
        }

        private void processFiles(List<ProductDTO.Image> images, Long productId) {
                List<String> fileIds = images != null ? images.stream()
                                .map(ProductDTO.Image::getFileId)
                                .filter(id -> id != null && !id.isEmpty())
                                .collect(Collectors.toList()) : new ArrayList<>();

                com.nanum.domain.file.model.ReferenceType type = com.nanum.domain.file.model.ReferenceType.PRODUCT;

                // syncFiles 호출로 기존 파일 중 제외된 파일들은 실제 삭제 처리
                fileService.syncFiles(fileIds, type, String.valueOf(productId));

                if (images != null) {
                        for (ProductDTO.Image img : images) {
                                if (img.getFileId() != null && "MAIN".equals(img.getType())) {
                                        fileService.setMainImage(img.getFileId(), type, String.valueOf(productId));
                                }
                        }
                }
        }

        @Transactional
        public void updateProductStatus(Long id, ProductStatus status) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
                product.update(product.getName(), product.getBrandName(), product.getSupplyPrice(),
                                product.getMapPrice(),
                                product.getRetailPrice(),
                                product.getSuggestedPrice(),
                                product.getSafetyStock(),
                                product.getDescription(),
                                status, product.getApplyYn(),
                                product.getReviewYn(),
                                product.getDeliveryWay(),
                                product.getDeliveryArea(),
                                product.getDeliveryType(),
                                product.getBundleShippingYn(),
                                product.getDeliveryPolicyType(),
                                product.getDeliveryMinOrderFee(),
                                product.getOutboundShipmentCode(),
                                product.getInboundShipmentCode(),
                                product.getDeliveryFee(),
                                product.getReturnFee(),
                                product.getExchangeFee(),
                                product.getDeliveryIslandYn(),
                                product.getDeliveryIslandFee());
        }

        @Transactional
        public void deleteProduct(Long productId, String siteCd) {
                Manager manager = getCurrentManager();
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

                List<ProductSite> sites = productSiteRepository.findByProductAndSiteCd(product, siteCd);
                if (sites.isEmpty()) {
                        throw new IllegalArgumentException("선택된 사이트의 상품 정보가 존재하지 않습니다.");
                }

                for (ProductSite ps : sites) {
                        ps.delete(manager.getManagerCode());
                }
        }

        @Transactional
        public void updateProductSitePrice(Long id, String siteCd, ProductSitePriceUpdateDTO request) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

                // 1. 해당 상품과 사이트 매핑 정보 업데이트
                List<ProductSite> sites = productSiteRepository.findByProductAndSiteCd(product, siteCd);
                if (sites.isEmpty()) {
                        throw new IllegalArgumentException("선택된 사이트의 상품 정보가 존재하지 않습니다.");
                }
                ProductSite productSite = sites.get(0); // 통상적으로 한 쌍이 매핑됨

                // 기존 판매가(salePrice)는 유지하면서 전달받은 가격/상태값 변경
                productSite.update(
                                request.getViewYn() != null ? request.getViewYn() : productSite.getViewYn(),
                                request.getAPrice(),
                                request.getBPrice(),
                                request.getCPrice());
                // 옵션별 가격 정보 일괄 업데이트 로직 제거 완료
        }

        @Transactional
        public void createBulkProductSites(Long productId, ProductSiteBulkCreateDTO request) {
                Manager manager = getCurrentManager();
                if (com.nanum.admin.manager.entity.ManagerType.MASTER != manager.getMbType()) {
                        throw new BusinessException("가격 일괄 설정 권한이 없습니다.", ErrorCode.ACCESS_DENIED);
                }

                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

                // 기존 등록된 사이트 목록 조회 (등록 무시 용도)
                List<String> existingSiteCds = productSiteRepository.findByProduct(product)
                                .stream()
                                .map(ProductSite::getSiteCd)
                                .collect(Collectors.toList());

                List<ProductSite> newSites = new ArrayList<>();
                for (ProductSiteBulkCreateDTO.SitePriceReq siteReq : request.getSitePrices()) {
                        String siteCd = siteReq.getSiteCd();
                        // 피드백: 이미 등록된 사이트이면 생성하지 않고 무시
                        if (existingSiteCds.contains(siteCd)) {
                                continue;
                        }

                        ProductSite newSite = ProductSite.builder()
                                        .product(product)
                                        .siteCd(siteCd)
                                        .aPrice(siteReq.getAPrice() != null ? siteReq.getAPrice() : BigDecimal.ZERO)
                                        .bPrice(siteReq.getBPrice() != null ? siteReq.getBPrice() : BigDecimal.ZERO)
                                        .cPrice(siteReq.getCPrice() != null ? siteReq.getCPrice() : BigDecimal.ZERO)
                                        .pdtClick(0)
                                        .viewYn("Y") // 일괄 등록 시 기본 노출 처리
                                        .build();

                        // 공통 옵션 처리 부분 제거됨
                        newSites.add(newSite);
                }

                if (!newSites.isEmpty()) {
                        productSiteRepository.saveAll(newSites);
                }
        }

        @Transactional
        public void deleteMasterProduct(Long id) {
                Manager manager = getCurrentManager();
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

                product.delete(manager.getManagerCode());
                fileService.deleteByReference(com.nanum.domain.file.model.ReferenceType.PRODUCT, String.valueOf(id),
                                manager.getManagerId());
        }

        private Manager getCurrentManager() {
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof CustomManagerDetails) {
                        return ((CustomManagerDetails) principal).getManager();
                }
                throw new IllegalStateException("인증된 관리자 정보가 없습니다.");
        }
}
