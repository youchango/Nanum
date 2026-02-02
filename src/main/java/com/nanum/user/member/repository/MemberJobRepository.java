package com.nanum.user.member.repository;

import com.nanum.user.member.model.MemberJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberJobRepository extends JpaRepository<MemberJob, Integer> {
    List<MemberJob> findByMemberId(Long memberId);
}
