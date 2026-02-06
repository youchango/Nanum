package com.nanum.domain.product.repository;

import com.nanum.domain.product.dto.AdminProductListDTO;
import com.nanum.domain.product.dto.AdminProductSearchDTO;
import com.querydsl.core.types.Projections;
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

import static com.nanum.domain.product.model.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminProductListDTO> findAdminProducts(AdminProductSearchDTO searchDTO, Pageable pageable) {
        List<AdminProductListDTO> content = queryFactory
                .select(Projections.fields(AdminProductListDTO.class,
                        product.id,
                        product.category.categoryId,
                        product.category.categoryName,
                        product.name,
                        product.price,
                        product.salePrice,
                        product.status,
                        product.viewCount,
                        product.createdAt,
                        product.updatedAt,
                        product.deleteYn))
                .from(product)
                .where(
                        eqCategoryId(searchDTO.getCategoryId()),
                        searchKeyword(searchDTO.getSearchType(), searchDTO.getSearchKeyword()),
                        eqStatus(searchDTO.getStatus()),
                        betweenDate(searchDTO.getStartDate(), searchDTO.getEndDate()),
                        product.deleteYn.eq("N"))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.createdAt.desc())
                .fetch();

        long total = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        eqCategoryId(searchDTO.getCategoryId()),
                        searchKeyword(searchDTO.getSearchType(), searchDTO.getSearchKeyword()),
                        eqStatus(searchDTO.getStatus()),
                        betweenDate(searchDTO.getStartDate(), searchDTO.getEndDate()),
                        product.deleteYn.eq("N"))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression eqCategoryId(Long categoryId) {
        if (categoryId == null)
            return null;
        return product.category.categoryId.eq(categoryId);
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
}
