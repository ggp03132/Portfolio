package com.project.lms.member;

import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberSecurityService implements UserDetailsService {

	private final MemberRepository mr;
	
	@Override
	public UserDetails loadUserByUsername(String memberid) throws UsernameNotFoundException {
		 Member member = mr.findByMemberid(memberid)
		            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + memberid));

        //Member 엔티티에서 저장된 Role enum을 가져와 이름을 대문자 문자열로 변환
        String roleName = member.getRole().name(); //Role.ADMIN -> "ADMIN"

        //Spring Security는 ROLE_ 접두사를 붙인 문자열을 최종 권한 이름 만듦
        String authorityName = "ROLE_" + roleName; //("ROLE_ADMIN", "ROLE_USER", "ROLE_INSTRUCTOR")

        //SimpleGrantedAuthority 객체를 생성하여 권한을 부여
        GrantedAuthority authority = new SimpleGrantedAuthority(authorityName);
        
        return new User(
            member.getMemberid(), 
            member.getPwd(), 
            Collections.singleton(authority) 
        );
	}
}