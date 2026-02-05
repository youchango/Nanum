package com.nanum.user.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanum.domain.point.model.Point;

public interface PointRepository extends JpaRepository<Point, Long>, PointRepositoryCustom {
}
