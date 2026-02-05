package com.nanum.domain.product.repository;

import com.nanum.domain.product.dto.AdminProductListDTO;
import com.nanum.domain.product.dto.AdminProductSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<AdminProductListDTO> findAdminProducts(AdminProductSearchDTO searchDTO, Pageable pageable);
}
