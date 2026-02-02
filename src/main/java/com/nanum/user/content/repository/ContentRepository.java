package com.nanum.user.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nanum.user.content.model.Content;

@Repository
public interface ContentRepository extends JpaRepository<Content, Integer>, ContentRepositoryCustom {
}
