package com.nanum.domain.product.repository;

import com.nanum.domain.product.dto.AdminProductListDTO;
import com.nanum.domain.product.dto.AdminProductSearchDTO;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.nanum.domain.product.model.ProductStatus;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.querydsl.jpa.JPAExpressions;
import com.nanum.domain.product.model.Product;
import com.nanum.domain.product.dto.ProductSitePriceDTO;
import com.nanum.domain.product.model.QProductOption;

import com.nanum.domain.product.model.QProductSite;

import static com.nanum.domain.product.model.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminProductListDTO> findAdminProducts(AdminProductSearchDTO searchDTO, Pageable pageable) {
        QProductSite productSite = QProductSite.productSite;

        // 1. 단일 Product 엔티티 페이징 조회 (조건 필터링)
        List<Product> products = queryFactory
                .selectFrom(product)
                .where(
                        inCategoryIds(searchDTO.getCategoryIds(), searchDTO.getCategoryId()),
                        searchKeyword(searchDTO.getSearchType(), searchDTO.getSearchKeyword()),
                        eqStatus(searchDTO.getStatus()),
                        betweenDate(searchDTO.getStartDate(), searchDTO.getEndDate()),
                        eqSiteCd(searchDTO.getSiteCd()),
                        product.deleteYn.eq("N"))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.createdAt.desc())
                .fetch();

        // 2. 전체 개수 쿼리
        long total = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        inCategoryIds(searchDTO.getCategoryIds(), searchDTO.getCategoryId()),
                        searchKeyword(searchDTO.getSearchType(), searchDTO.getSearchKeyword()),
                        eqStatus(searchDTO.getStatus()),
                        betweenDate(searchDTO.getStartDate(), searchDTO.getEndDate()),
                        eqSiteCd(searchDTO.getSiteCd()),
                        product.deleteYn.eq("N"))
                .fetchOne();

        if (products.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        // 3. 조회된 상품 ID 목록 추출
        List<Long> productIds = products.stream().map(Product::getId).collect(Collectors.toList());

        // 4. 상품 ID들에 해당하는 ProductSite + Option 가격 일괄 조회
        QProductOption productOption = QProductOption.productOption;

        // 메모리 조합용 (상품 ID별 ProductSitePriceDTO 목록)
        List<com.querydsl.core.Tuple> siteTuples = queryFactory
                .select(
                        productSite.product.id,
                        productOption.id,
                        productOption.name,
                        productSite.standardPrice,
                        productSite.aPrice,
                        productSite.bPrice,
                        productSite.cPrice)
                .from(productSite)
                .leftJoin(productOption).on(productSite.optionId.eq(productOption.id))
                .where(
                        productSite.product.id.in(productIds),
                        StringUtils.hasText(searchDTO.getSiteCd()) ? productSite.siteCd.eq(searchDTO.getSiteCd())
                                : null)
                .fetch();

        // 상품 ID 단위로 Grouping
        Map<Long, List<ProductSitePriceDTO>> sitePriceMap = siteTuples.stream()
                .collect(Collectors.groupingBy(
                        t -> t.get(productSite.product.id),
                        Collectors.mapping(t -> ProductSitePriceDTO.builder()
                                .optionId(t.get(productOption.id))
                                .optionName(t.get(productOption.name) != null ? t.get(productOption.name) : "")
                                .standardPrice(
                                        t.get(productSite.standardPrice) != null ? t.get(productSite.standardPrice) : 0)
                                .aPrice(t.get(productSite.aPrice))
                                .bPrice(t.get(productSite.bPrice))
                                .cPrice(t.get(productSite.cPrice))
                                .build(), Collectors.toList())));

        // 5. 최종 DTO 매핑
        List<AdminProductListDTO> content = products.stream().map(p -> {
            String catName = p.getCategories().isEmpty() ? "" : p.getCategories().get(0).getCategoryName();
            Long catId = p.getCategories().isEmpty() ? null : p.getCategories().get(0).getCategoryId();
            return AdminProductListDTO.builder()
                    .id(p.getId())
                    .categoryId(catId)
                    .categoryName(catName)
                    .name(p.getName())
                    .supplyPrice(p.getSupplyPrice())
                    .mapPrice(p.getMapPrice())
                    .standardPrice(p.getStandardPrice())
                    .status(p.getStatus())
                    .viewCount(p.getViewCount())
                    .createdAt(p.getCreatedAt())
                    .updatedAt(p.getUpdatedAt())
                    .deleteYn(p.getDeleteYn())
                    .sitePrices(sitePriceMap.getOrDefault(p.getId(), List.of()))
                    .build();
        }).collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression inCategoryIds(List<Long> categoryIds, Long singleCategoryId) {
        if (categoryIds != null && !categoryIds.isEmpty()) {
            return product.categories.any().categoryId.in(categoryIds);
        } else if (singleCategoryId != null) {
            return product.categories.any().categoryId.eq(singleCategoryId);
        }
        return null;
    }

    private BooleanExpression searchKeyword(String searchType, String keyword) {
        if (!StringUtils.hasText(keyword))
            return null;
        if ("NAME".equals(searchType)) {
            return product.name.contains(keyword);
        }
        return null;
    }

    private BooleanExpression eqStatus(ProductStatus status) {
        if (status == null)
            return null;
        return product.status.eq(status);
    }

    private BooleanExpression betweenDate(String startDate, String endDate) {
        if (!StringUtils.hasText(startDate) || !StringUtils.hasText(endDate))
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        return product.createdAt.goe(start.atStartOfDay()).and(product.createdAt.lt(end.plusDays(1).atStartOfDay()));
    }

    private BooleanExpression eqSiteCd(String siteCd) {
        if (!StringUtils.hasText(siteCd))
            return null;
        QProductSite productSite = QProductSite.productSite;
        return JPAExpressions.selectOne()
                .from(productSite)
                .where(productSite.product.eq(product).and(productSite.siteCd.eq(siteCd)))
                .exists();
    }
}
