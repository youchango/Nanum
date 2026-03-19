package com.nanum.domain.content.repository;

import com.nanum.domain.content.model.Content;
import com.nanum.domain.content.model.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByType(ContentType type);

    List<Content> findByTypeAndSiteCd(ContentType type, String siteCd);

    List<Content> findBySiteCd(String siteCd);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Content c WHERE c.type = :type AND c.siteCd = :siteCd AND c.deleteYn = 'N' AND (c.subject LIKE %:keyword% OR c.contentBody LIKE %:keyword%)")
    List<Content> findBySearch(ContentType type, String siteCd, String keyword);
}
