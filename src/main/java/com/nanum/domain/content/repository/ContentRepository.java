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

    List<Content> findByTypeAndSiteCd(ContentType type, String siteCd);

    Page<Content> findByTypeAndSiteCdAndDeleteYnOrderByCreatedAtDesc(ContentType type, String siteCd, String deleteYn, Pageable pageable);
}
