package com.nanum.user.point.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nanum.domain.point.dto.PointSearchDto;
import com.nanum.domain.point.model.Point;

public interface PointRepositoryCustom {
    Page<Point> searchPoints(PointSearchDto searchDto, Pageable pageable);
}
