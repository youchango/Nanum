package com.nanum.user.product.repository;

import com.nanum.admin.product.dto.AdminProductListDTO;
import com.nanum.admin.product.dto.AdminProductSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<AdminProductListDTO> findAdminProducts(AdminProductSearchDTO searchDTO, Pageable pageable);
}
