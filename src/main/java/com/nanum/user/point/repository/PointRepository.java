package com.nanum.user.point.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanum.domain.point.model.Point;

public interface PointRepository extends JpaRepository<Point, Long>, PointRepositoryCustom {
    List<Point> findTop5BySiteCdOrderByCreatedAtDesc(String siteCd);

    List<Point> findTop5ByOrderByCreatedAtDesc();
}
