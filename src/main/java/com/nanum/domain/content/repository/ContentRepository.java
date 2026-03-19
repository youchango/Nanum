package com.nanum.domain.content.repository;

import com.nanum.domain.content.model.Content;
import com.nanum.domain.content.model.ContentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
        List<Content> findByType(ContentType type);

        Page<Content> findByTypeAndSiteCdAndDeleteYnOrderByCreatedAtDesc(ContentType type, String siteCd,
                        String deleteYn,
                        Pageable pageable);

        @org.springframework.data.jpa.repository.Query("SELECT c FROM Content c " +
                        "WHERE c.type = :type AND c.siteCd = :siteCd AND c.deleteYn = :deleteYn " +
                        "AND (:keyword IS NULL OR :keyword = '' OR c.subject LIKE %:keyword% OR c.contentBody LIKE %:keyword%)")
        Page<Content> findBySearch(
                        @org.springframework.data.repository.query.Param("type") ContentType type,
                        @org.springframework.data.repository.query.Param("siteCd") String siteCd,
                        @org.springframework.data.repository.query.Param("deleteYn") String deleteYn,
                        @org.springframework.data.repository.query.Param("keyword") String keyword,
                        Pageable pageable);
}
