package com.nanum.admin.inout.service;

import com.nanum.admin.inout.dto.*;
import com.nanum.domain.inout.model.InoutDetail;
import com.nanum.domain.inout.model.InoutMaster;
import com.nanum.domain.inout.model.QInoutDetail;
import com.nanum.domain.inout.model.QInoutMaster;
import com.nanum.domain.product.model.QProduct;
import com.nanum.domain.product.model.QProductOption;
import com.nanum.domain.product.model.QProductStock;
import com.nanum.admin.inout.repository.InoutDetailRepository;
import com.nanum.admin.inout.repository.InoutRepository;
import com.nanum.admin.manager.entity.QManager;
import com.nanum.admin.manager.entity.QManagerScm;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.nanum.domain.product.model.Product;
import com.nanum.domain.product.model.ProductOption;
import com.nanum.domain.product.model.ProductStock;
import com.nanum.domain.product.repository.ProductOptionRepository;
import com.nanum.domain.product.repository.ProductRepository;
import com.nanum.domain.product.repository.ProductStockRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.service.CustomManagerDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.querydsl.core.types.dsl.Expressions;

/**
 * 입출고 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminInoutService {

    private final InoutRepository inoutRepository;
    private final InoutDetailRepository inoutDetailRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductStockRepository productStockRepository;
    private final JPAQueryFactory queryFactory;

    /**
     * 입고 등록 처리
     */
    @Transactional
    public String registerInbound(InboundRequest request) {
        String ioCode = generateIoCode();
        Manager manager = getCurrentManager();

        InoutMaster master = InoutMaster.builder()
                .ioCode(ioCode)
                .ioType("IN")
                .ioCategory(request.getIoCategory())
                .ioDate(LocalDateTime.now())
                .managerCode(manager.getManagerCode())
                .build();
        inoutRepository.save(master);

        int seq = 1;
        for (InboundRequest.InboundItem item : request.getItems()) {
            InoutDetail detail = InoutDetail.builder()
                    .ioCode(ioCode)
                    .no(seq++)
                    .ioType("IN")
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .optionId(item.getOptionId())
                    .optionName(item.getOptionName())
                    .brandName(item.getBrandName())
                    .qty(item.getQty())
                    .realQty(item.getQty())
                    .memo(item.getMemo())
                    .build();
            inoutDetailRepository.save(detail);

            // 재고 업데이트
            updateInoutStock(item.getProductId(), item.getOptionId(), item.getQty(), "IN");
        }

        return ioCode;
    }

    /**
     * 출고 등록 처리
     */
    @Transactional
    public String registerOutbound(OutboundRequest request) {
        String ioCode = generateIoCode();
        Manager manager = getCurrentManager();

        InoutMaster master = InoutMaster.builder()
                .ioCode(ioCode)
                .ioType("OUT")
                .ioCategory(request.getIoCategory())
                .ioDate(LocalDateTime.now())
                .managerCode(manager.getManagerCode())
                .build();
        inoutRepository.save(master);

        int seq = 1;
        for (OutboundRequest.OutboundItem item : request.getItems()) {
            // 원천 입고 데이터 차감
            InoutDetail inboundDetail = inoutDetailRepository.findById(
                    new com.nanum.domain.inout.model.InoutDetailId(item.getInIoCode(), item.getInNo()))
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 입고 내역입니다."));

            inboundDetail.decreaseRealQty(item.getQty());
            inoutDetailRepository.save(inboundDetail);

            InoutDetail detail = InoutDetail.builder()
                    .ioCode(ioCode)
                    .no(seq++)
                    .ioType("OUT")
                    .inIoCode(item.getInIoCode())
                    .inNo(item.getInNo())
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .optionId(item.getOptionId())
                    .optionName(item.getOptionName())
                    .brandName(item.getBrandName())
                    .qty(item.getQty())
                    .memo(item.getMemo())
                    .build();
            inoutDetailRepository.save(detail);

            // 재고 업데이트
            updateInoutStock(item.getProductId(), item.getOptionId(), item.getQty(), "OUT");
        }

        return ioCode;
    }

    /**
     * 입고/출고 상세 목록 조회 (Inbound/Outbound Management용)
     */
    @Transactional(readOnly = true)
    public Page<InoutDetailResponse> searchInoutDetails(InoutSearchDTO searchDTO) {
        QInoutDetail detail = QInoutDetail.inoutDetail;
        QInoutMaster master = QInoutMaster.inoutMaster;
        QManagerScm scm = QManagerScm.managerScm;
        QManager manager = QManager.manager;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(detail.ioType.eq(searchDTO.getIoType()));

        if (searchDTO.getSearchKeyword() != null && !searchDTO.getSearchKeyword().isEmpty()) {
            String keyword = searchDTO.getSearchKeyword();
            if ("all".equalsIgnoreCase(searchDTO.getSearchType())) {
                builder.and(detail.ioCode.contains(keyword)
                        .or(detail.productName.contains(keyword))
                        .or(detail.brandName.contains(keyword)));
            } else if ("ioCode".equals(searchDTO.getSearchType())) {
                builder.and(detail.ioCode.contains(keyword));
            } else if ("productName".equals(searchDTO.getSearchType())) {
                builder.and(detail.productName.contains(keyword));
            } else if ("brandName".equals(searchDTO.getSearchType())) {
                builder.and(detail.brandName.contains(keyword));
            }
        }

        if (searchDTO.getProductId() != null) {
            builder.and(detail.productId.eq(searchDTO.getProductId()));
        }

        if (searchDTO.getOptionId() != null) {
            builder.and(detail.optionId.eq(searchDTO.getOptionId()));
        } else if (searchDTO.getProductId() != null) {
            // 옵션 ID가 없고 상품 ID만 있는 경우, 해당 상품의 단품 내역(optionId is null)만 조회할지 여부 결정
            // 여기서는 사용자의 요구사항에 따라 옵션 상품이 아닌 경우 productId만 같은 것을 조회하므로 추가 조건 없음
        }

        List<InoutDetailResponse> list = queryFactory
                .select(Projections.fields(InoutDetailResponse.class,
                        detail.ioCode,
                        detail.no,
                        detail.ioType,
                        master.ioCategory,
                        scm.supplierName,
                        new CaseBuilder()
                                .when(scm.supplierName.isNull().or(scm.supplierName.eq("")))
                                .then(manager.managerName)
                                .otherwise(scm.supplierName).as("supplierName"),
                        detail.productName,
                        detail.optionName,
                        detail.brandName,
                        detail.qty,
                        detail.realQty,
                        detail.memo,
                        detail.inIoCode,
                        detail.inNo,
                        detail.createdAt))
                .from(detail)
                .join(master).on(detail.ioCode.eq(master.ioCode))
                .leftJoin(scm).on(master.managerCode.eq(scm.managerCode))
                .leftJoin(manager).on(master.managerCode.eq(manager.managerCode))
                .where(builder)
                .orderBy(detail.createdAt.desc())
                .offset(searchDTO.getOffset())
                .limit(searchDTO.getPageSize())
                .fetch();

        // RowNum 부여 및 Display 코드 생성
        long total = queryFactory.select(detail.count()).from(detail).where(builder).fetchOne();
        long startNum = total - searchDTO.getOffset();
        for (InoutDetailResponse item : list) {
            item.setRowNum(startNum--);
            item.setDisplayIoCode(item.getIoCode() + " - " + item.getNo());
            if (item.getInIoCode() != null) {
                item.setDisplayInIoCode(item.getInIoCode() + " - " + item.getInNo());
            }
        }

        return new PageImpl<>(list, PageRequest.of(searchDTO.getPage() - 1, searchDTO.getPageSize()), total);
    }

    /**
     * 입출고 현황 조회 (Inout Status)
     * Master 기반으로 조회하되, 상품 정보를 위해 Detail 조인
     */
    @Transactional(readOnly = true)
    public Page<InoutStatusResponse> searchInoutStatus(InoutSearchDTO searchDTO) {
        QInoutMaster master = QInoutMaster.inoutMaster;
        QInoutDetail detail = QInoutDetail.inoutDetail;
        QManagerScm scm = QManagerScm.managerScm;
        QManager manager = QManager.manager;

        BooleanBuilder builder = new BooleanBuilder();
        if (searchDTO.getIoType() != null && !searchDTO.getIoType().isEmpty()) {
            builder.and(master.ioType.eq(searchDTO.getIoType()));
        }

        if (searchDTO.getSearchKeyword() != null && !searchDTO.getSearchKeyword().isEmpty()) {
            String keyword = searchDTO.getSearchKeyword();
            if ("all".equalsIgnoreCase(searchDTO.getSearchType())) {
                builder.and(master.ioCode.contains(keyword)
                        .or(detail.productName.contains(keyword))
                        .or(detail.brandName.contains(keyword)));
            } else if ("ioCode".equals(searchDTO.getSearchType())) {
                builder.and(master.ioCode.contains(keyword));
            } else if ("productName".equals(searchDTO.getSearchType())) {
                builder.and(detail.productName.contains(keyword));
            } else if ("brandName".equals(searchDTO.getSearchType())) {
                builder.and(detail.brandName.contains(keyword));
            }
        }

        // Master 기준 중복 제거를 위해 groupBy 사용하거나, fetchJoin 등으로 처리
        // 여기서는 Master 기준으로 리스트를 가져오고 첫 번째 상세 품목을 대표로 표시하도록 구현
        List<InoutStatusResponse> list = queryFactory
                .select(Projections.fields(InoutStatusResponse.class,
                        master.ioCode.as("ioCode"),
                        master.ioType.as("ioType"),
                        master.ioCategory.as("ioCategory"),
                        detail.brandName.as("brandName"),
                        detail.productName.as("productName"),
                        new CaseBuilder()
                                .when(scm.supplierName.isNull().or(scm.supplierName.eq("")))
                                .then(manager.managerName)
                                .otherwise(scm.supplierName).as("supplierName"),
                        master.createdAt.as("createdAt")))
                .from(master)
                .leftJoin(detail).on(master.ioCode.eq(detail.ioCode).and(detail.no.eq(1)))
                .leftJoin(scm).on(master.managerCode.eq(scm.managerCode))
                .leftJoin(manager).on(master.managerCode.eq(manager.managerCode))
                .where(builder)
                .orderBy(master.createdAt.desc())
                .offset(searchDTO.getOffset())
                .limit(searchDTO.getPageSize())
                .fetch();

        long total = queryFactory.select(master.count()).from(master).where(builder).fetchOne();
        long startNum = total - searchDTO.getOffset();
        for (InoutStatusResponse item : list) {
            item.setRowNum(startNum--);
        }

        return new PageImpl<>(list, PageRequest.of(searchDTO.getPage() - 1, searchDTO.getPageSize()), total);
    }

    /**
     * 입출고 상세 내역 조회 (Modal용)
     */
    @Transactional(readOnly = true)
    public List<InoutDetailResponse> getInoutDetails(String ioCode) {
        QInoutDetail detail = QInoutDetail.inoutDetail;
        QManagerScm scm = QManagerScm.managerScm;
        QInoutMaster master = QInoutMaster.inoutMaster;
        QManager manager = QManager.manager;

        return queryFactory
                .select(Projections.fields(InoutDetailResponse.class,
                        detail.ioCode.as("ioCode"),
                        detail.no.as("no"),
                        detail.ioType.as("ioType"),
                        master.ioCategory.as("ioCategory"),
                        detail.productName.as("productName"),
                        detail.optionName.as("optionName"),
                        detail.brandName.as("brandName"),
                        detail.qty.as("qty"),
                        new CaseBuilder()
                                .when(scm.supplierName.isNull().or(scm.supplierName.eq("")))
                                .then(manager.managerName)
                                .otherwise(scm.supplierName).as("supplierName"),
                        detail.memo.as("memo"),
                        detail.createdAt.as("createdAt")))
                .from(detail)
                .join(master).on(detail.ioCode.eq(master.ioCode))
                .leftJoin(scm).on(master.managerCode.eq(scm.managerCode))
                .leftJoin(manager).on(master.managerCode.eq(manager.managerCode))
                .where(detail.ioCode.eq(ioCode))
                .orderBy(detail.no.asc())
                .fetch();
    }

    /**
     * 입출고용 상품 검색 (실재고 포함)
     */
    @Transactional(readOnly = true)
    public Page<InoutProductResponse> searchProductsForInout(InoutSearchDTO searchDTO) {
        QProduct product = QProduct.product;
        QProductStock stock = QProductStock.productStock;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(product.deleteYn.eq("N"));

        if (searchDTO.getSearchKeyword() != null && !searchDTO.getSearchKeyword().isEmpty()) {
            String keyword = searchDTO.getSearchKeyword();
            if ("all".equalsIgnoreCase(searchDTO.getSearchType())) {
                builder.and(product.name.contains(keyword)
                        .or(product.brandName.contains(keyword)));
            } else if ("productName".equals(searchDTO.getSearchType())) {
                builder.and(product.name.contains(keyword));
            } else if ("brandName".equals(searchDTO.getSearchType())) {
                builder.and(product.brandName.contains(keyword));
            }
        }

        List<InoutProductResponse> list = queryFactory
                .select(Projections.fields(InoutProductResponse.class,
                        product.id.as("productId"),
                        product.name.as("productName"),
                        product.brandName,
                        product.stockQuantity,
                        Expressions.numberTemplate(Long.class, "coalesce(sum({0}), 0L)", stock.stockQuantity)
                                .as("realStock"),
                        product.safetyStock,
                        product.status.stringValue().as("status"),
                        product.optionYn))
                .from(product)
                .leftJoin(stock).on(product.id.eq(stock.product.id))
                .where(builder)
                .groupBy(product.id)
                .orderBy(product.createdAt.desc())
                .offset(searchDTO.getOffset())
                .limit(searchDTO.getPageSize())
                .fetch();

        // 옵션 정보 추가 조회 (필요 시)
        for (InoutProductResponse item : list) {
            if ("Y".equals(item.getOptionYn())) {
                item.setOptions(getOptionsWithRealStock(item.getProductId()));
            }
        }

        long total = queryFactory.select(product.count()).from(product).where(builder).fetchOne();
        return new PageImpl<>(list, PageRequest.of(searchDTO.getPage() - 1, searchDTO.getPageSize()), total);
    }

    private List<InoutProductResponse.OptionResponse> getOptionsWithRealStock(Long productId) {
        QProductOption option = QProductOption.productOption;
        QProductStock stock = QProductStock.productStock;

        return queryFactory
                .select(Projections.fields(InoutProductResponse.OptionResponse.class,
                        option.id.as("optionId"),
                        option.title1, option.name1,
                        option.title2, option.name2,
                        option.title3, option.name3,
                        option.stockQuantity,
                        Expressions.numberTemplate(Long.class, "coalesce({0}, 0L)", stock.stockQuantity)
                                .as("realStock")))
                .from(option)
                .leftJoin(stock).on(option.id.eq(stock.option.id))
                .where(option.product.id.eq(productId).and(option.useYn.eq("Y")))
                .fetch();
    }

    /**
     * 출고 등록용 입고 내역 검색 (잔량 > 0)
     */
    @Transactional(readOnly = true)
    public Page<InoutDetailResponse> searchInboundForOutbound(InoutSearchDTO searchDTO) {
        QInoutDetail detail = QInoutDetail.inoutDetail;
        QInoutMaster master = QInoutMaster.inoutMaster;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(detail.ioType.eq("IN"));
        builder.and(detail.realQty.gt(0)); // 재고 잔량이 있는 것만

        if (searchDTO.getSearchKeyword() != null && !searchDTO.getSearchKeyword().isEmpty()) {
            String keyword = searchDTO.getSearchKeyword();
            if ("all".equalsIgnoreCase(searchDTO.getSearchType())) {
                builder.and(detail.ioCode.contains(keyword)
                        .or(detail.productName.contains(keyword))
                        .or(detail.brandName.contains(keyword)));
            } else if ("productName".equals(searchDTO.getSearchType())) {
                builder.and(detail.productName.contains(keyword));
            } else if ("brandName".equals(searchDTO.getSearchType())) {
                builder.and(detail.brandName.contains(keyword));
            } else if ("ioCode".equals(searchDTO.getSearchType())) {
                builder.and(detail.ioCode.contains(keyword));
            }
        }

        List<InoutDetailResponse> list = queryFactory
                .select(Projections.fields(InoutDetailResponse.class,
                        detail.ioCode,
                        detail.no,
                        detail.ioType,
                        master.ioCategory,
                        detail.productName,
                        detail.optionName,
                        detail.brandName,
                        detail.qty,
                        detail.realQty,
                        detail.createdAt))
                .from(detail)
                .join(master).on(detail.ioCode.eq(master.ioCode))
                .where(builder)
                .orderBy(detail.createdAt.asc()) // FIFO를 위해 선입고 순서로 정렬 권장
                .offset(searchDTO.getOffset())
                .limit(searchDTO.getPageSize())
                .fetch();

        for (InoutDetailResponse item : list) {
            item.setDisplayIoCode(item.getIoCode() + " - " + item.getNo());
        }

        long total = queryFactory.select(detail.count()).from(detail).where(builder).fetchOne();
        return new PageImpl<>(list, PageRequest.of(searchDTO.getPage() - 1, searchDTO.getPageSize()), total);
    }

    private String generateIoCode() {
        Optional<InoutMaster> lastMaster = inoutRepository.findFirstByOrderByIoCodeDesc();
        int nextId = 1;
        if (lastMaster.isPresent()) {
            String lastCode = lastMaster.get().getIoCode();
            try {
                if (lastCode != null && lastCode.length() > 2) {
                    nextId = Integer.parseInt(lastCode.substring(2)) + 1;
                }
            } catch (Exception e) {
                log.warn("마지막 입출고 코드 분석 실패: {}", lastCode);
            }
        }
        return String.format("IO%08d", nextId);
    }

    /**
     * 상품 생성/수정 시 초기 재고 생성 및 전환 대응
     * - 기존 재고(stock_quantity)는 유지함 (Update 시 덮어쓰지 않음)
     * - 전환 시나리오(단품<->옵션) 발생 시 불필요한 기존 데이터 정리 후 신규 생성(0)
     */
    public void initializeStock(Product product, List<ProductOption> options) {
        // 1. 현재 이 상품에 저장된 모든 재고 레코드를 가져옴
        List<ProductStock> existingStocks = productStockRepository.findByProduct(product);

        if (options == null || options.isEmpty()) {
            // [옵션 미사용 상품]
            // 기존에 옵션별 재고가 있었다면 모두 삭제
            List<ProductStock> toDelete = existingStocks.stream()
                    .filter(s -> s.getOption() != null)
                    .collect(java.util.stream.Collectors.toList());
            if (!toDelete.isEmpty()) {
                productStockRepository.deleteAll(toDelete);
            }

            // 기존에 단품 재고가 없으면 새로 생성(0)
            boolean hasBaseStock = existingStocks.stream().anyMatch(s -> s.getOption() == null);
            if (!hasBaseStock) {
                ProductStock stock = ProductStock.builder()
                        .product(product)
                        .option(null)
                        .stockQuantity(0)
                        .build();
                productStockRepository.save(stock);
            } else {
                log.debug("상품 {}의 기존 재고 데이터가 존재하여 생성을 건너뜜", product.getId());
            }
        } else {
            // [옵션 사용 상품]
            // 기존에 단품 재고(option_id is null)가 있었다면 삭제
            List<ProductStock> baseStocks = existingStocks.stream()
                    .filter(s -> s.getOption() == null)
                    .collect(java.util.stream.Collectors.toList());
            if (!baseStocks.isEmpty()) {
                productStockRepository.deleteAll(baseStocks);
            }

            // 각 옵션별로 재고 레코드가 없으면 새로 생성(0)
            for (ProductOption option : options) {
                boolean exists = existingStocks.stream()
                        .anyMatch(s -> s.getOption() != null && s.getOption().getId().equals(option.getId()));

                if (!exists) {
                    ProductStock stock = ProductStock.builder()
                            .product(product)
                            .option(option)
                            .stockQuantity(0)
                            .build();
                    productStockRepository.save(stock);
                }
            }

            // (추가) 현재 옵션 리스트에 없는 기존 재고 레코드가 있다면 삭제 (옵션 삭제 대응)
            List<Long> currentOptionIds = options.stream()
                    .map(ProductOption::getId)
                    .filter(id -> id != null)
                    .collect(java.util.stream.Collectors.toList());

            List<ProductStock> orphanedStocks = existingStocks.stream()
                    .filter(s -> s.getOption() != null && !currentOptionIds.contains(s.getOption().getId()))
                    .collect(java.util.stream.Collectors.toList());

            if (!orphanedStocks.isEmpty()) {
                productStockRepository.deleteAll(orphanedStocks);
            }
        }
    }

    /**
     * 배송(출고)에 의한 실재고 차감 처리
     */
    @Transactional
    public void decreasePhysicalStock(Long productId, Long optionId, Integer qty, String remark) {
        updateInoutStock(productId, optionId, qty, "OUT");
    }

    public void updateInoutStock(Long productId, Long optionId, Integer qty, String type) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        ProductOption option = null;
        if (optionId != null) {
            option = productOptionRepository.findById(optionId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다."));
        }

        final ProductOption finalOption = option;
        // product_stock 업데이트 (중복 데이터가 있을 수 있으므로 리스트로 조회 후 첫 번째 것 사용)
        List<ProductStock> stockList = productStockRepository.findByProduct(product).stream()
                .filter(s -> (finalOption == null && s.getOption() == null) || 
                             (finalOption != null && s.getOption() != null && s.getOption().getId().equals(finalOption.getId())))
                .collect(java.util.stream.Collectors.toList());

        ProductStock stock;
        if (stockList.isEmpty()) {
            stock = ProductStock.builder()
                    .product(product)
                    .option(finalOption)
                    .stockQuantity(0)
                    .createdAt(LocalDateTime.now())
                    .build();
            productStockRepository.save(stock);
        } else {
            stock = stockList.get(0);
            if (stockList.size() > 1) {
                log.warn("상품 {} 옵션 {}에 대해 여러 개의 재고 데이터가 발견되었습니다. 첫 번째 데이터를 사용합니다.", productId, optionId);
            }
        }

        if ("IN".equals(type)) {
            stock.increaseStock(qty);
        } else {
            stock.decreaseStock(qty);
        }
        productStockRepository.save(stock);

        // 마스터 재고 동기화 (Display용)
        if (option != null) {
            option.updateStockQuantity(stock.getStockQuantity());
            productOptionRepository.save(option);

            // 상품 마스터의 총 재고 합산 업데이트 (옵션 상품인 경우)
            int totalStock = productStockRepository.findByProduct(product).stream()
                    .mapToInt(ProductStock::getStockQuantity)
                    .sum();
            product.updateStockQuantity(totalStock);
            productRepository.save(product);
        } else {
            product.updateStockQuantity(stock.getStockQuantity());
            productRepository.save(product);
        }
    }

    private Manager getCurrentManager() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomManagerDetails) {
            return ((CustomManagerDetails) principal).getManager();
        }
        throw new IllegalStateException("인증된 관리자 정보가 없습니다.");
    }
}
