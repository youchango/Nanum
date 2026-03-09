package com.nanum.domain.product.repository;

import com.nanum.domain.product.dto.AdminProductListDTO;
import com.nanum.domain.product.dto.AdminProductSearchDTO;
import com.nanum.domain.product.model.Product;
import java.util.List;

public interface ProductRepositoryCustom {
    List<AdminProductListDTO> findAdminProducts(AdminProductSearchDTO searchDTO);

    int countAdminProducts(AdminProductSearchDTO searchDTO);

    List<Product> findMallProducts(String siteCd, Long categoryId);
}
