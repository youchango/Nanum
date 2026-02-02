package com.nanum.user.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nanum.user.member.model.MemberJob;

import java.util.List;

@Repository
public interface MemberJobRepository extends JpaRepository<MemberJob, Integer> {
    List<MemberJob> findByMemberId(Long memberId);
}
