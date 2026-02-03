package com.nanum.user.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nanum.user.member.model.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String>, MemberRepositoryCustom {

    Optional<Member> findByMemberLogin(String memberLogin);

    boolean existsByMemberLogin(String memberLogin);
}
