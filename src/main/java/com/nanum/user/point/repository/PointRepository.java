package com.nanum.user.point.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nanum.domain.point.model.Point;

public interface PointRepository extends JpaRepository<Point, Long>, PointRepositoryCustom {
    List<Point> findTop5BySiteCdOrderByCreatedAtDesc(String siteCd);

    List<Point> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT COALESCE(SUM(CASE WHEN p.pointGubun = 'SAVE' THEN p.pointUse ELSE 0 END), 0) " +
            "- COALESCE(SUM(CASE WHEN p.pointGubun = 'USE' THEN p.pointUse ELSE 0 END), 0) " +
            "FROM Point p WHERE p.member.memberCode = :memberCode")
    int calculateBalance(@Param("memberCode") String memberCode);

    Page<Point> findByMemberMemberCodeOrderByCreatedAtDesc(String memberCode, Pageable pageable);
}
