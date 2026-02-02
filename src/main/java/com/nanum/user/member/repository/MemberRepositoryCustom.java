package com.nanum.user.member.repository;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.member.model.Member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<Member> searchMembers(SearchDTO searchDTO, Pageable pageable);
}
