package com.nanum.user.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nanum.user.code.model.Code;

import java.util.List;

@Repository
public interface CodeRepository extends JpaRepository<Code, Integer>, CodeRepositoryCustom {
    List<Code> findAllByCodeTypeAndDeleteYn(String codeType, String deleteYn);

    List<Code> findAllByDepthAndDeleteYn(Integer depth, String deleteYn);

    int countByUpperAndDeleteYn(Integer upper, String deleteYn);
}
