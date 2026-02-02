package com.nanum.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nanum.user.member.model.Member;
import com.nanum.user.member.repository.MemberRepository;

/**
 * Spring Security에서 사용자 정보를 로드하는 서비스입니다.
 * DB의 member 테이블에서 사용자 정보를 조회하여 UserDetails 객체로 반환합니다.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * 사용자 아이디(username)로 사용자 정보를 조회합니다.
     *
     * @param username 로그인 시 입력한 아이디
     * @return UserDetails 인터페이스를 구현한 CustomUserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DB에서 회원 정보 조회
        Member member = memberRepository.findByMemberLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // CustomUserDetails 객체 생성 및 반환
        return new CustomUserDetails(member);
    }
}
