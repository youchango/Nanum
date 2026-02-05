package com.nanum.user.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nanum.domain.member.model.Member;

import java.util.Optional;

import com.nanum.domain.member.model.MemberType;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByMemberId(String memberId);

    boolean existsByMemberId(String memberId);

    boolean existsByMemberIdAndMemberType(String memberId, MemberType memberType);

    Optional<Member> findTopByMemberCodeStartingWithOrderByMemberCodeDesc(String prefix);

    Optional<Member> findByMemberCode(String memberCode);
}
