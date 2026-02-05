package com.nanum.user.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanum.domain.member.model.MemberBiz;

public interface MemberBizRepository extends JpaRepository<MemberBiz, String> {
}
