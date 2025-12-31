package com.project.lms.member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.lms.DataNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository mr;
	private final PasswordEncoder pe;

	public void create(String memberid, String pwd, String name, String cellnum, String birth, String email) {
		Member m = new Member();
		m.setMemberid(memberid);
		m.setPwd(pe.encode(pwd)); // pe.encode 암호화
		m.setName(name);
		m.setCellnum(cellnum);
		m.setBirth(birth);
		m.setEmail(email);
		m.setRole(Role.USER);
		m.setCreDate(LocalDateTime.now());
		m.setModDate(LocalDateTime.now());
		mr.save(m);
	}

	public Page<Member> getList(int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("creDate"));

		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		return mr.findAll(pageable);

	}
	
	public Page<Member> getListByRole(Role role, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("memberno").descending());
        return mr.findByRoleValue(role, pageable);
    }
	
	// 검색 기능 추가
	public Page<Member> searchMembers(String keyword, int page) {
		Pageable pageable = PageRequest.of(page, 10, Sort.by("creDate").descending());
		return mr.searchMembers(keyword, pageable);
	}
	
	public Page<Member> searchMembersByRole(Role role, String keyword, int page) {
		Pageable pageable = PageRequest.of(page, 10, Sort.by("creDate").descending());
		return mr.searchMembersByRole(role, keyword, pageable);
	}

	public Member getMember(String memberid) {
		System.out.println("Searching for memberid: " + memberid);

		Optional<Member> om = mr.findByMemberid(memberid);

		if (om.isPresent()) {
			return om.get();
		} else {
			throw new DataNotFoundException("해당 ID의 회원이 존재하지 않습니다.");
		}
	}

	public Member getMember(Integer memberno) {
		return mr.findById(memberno)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 회원이 존재하지 않습니다."));
	}

	// 중복 체크, memberid는 수정시 예외
	public boolean isIdDuplicated(String memberid, Integer memberno) {
		if (memberno == null) { // 신규 가입 시: memberid가 DB에 존재하면 중복 true 반환
			return mr.findByMemberid(memberid).isPresent();
		} else {
			Optional<Member> memberOpt = mr.findByMemberid(memberid);
			if (memberOpt.isEmpty()) {
				return false;
				// DB에 memberid가 없으니 중복 아님
				// 같으면 → 본인 아이디이므로 중복 아님 → false
			}
			Member member = memberOpt.get();
			return !member.getMemberno().equals(memberno); // 다르면 → 다른 사람이 사용 중인 아이디 → 중복 → true
		}
	}

	public boolean isCellnumDuplicated(String cellnum, Integer memberno) {
		if (memberno == null) {
			return mr.findByCellnum(cellnum).isPresent();
		} else {
			Optional<Member> memberOpt = mr.findByCellnum(cellnum);
			if (memberOpt.isEmpty()) {
				return false;
			}
			Member member = memberOpt.get();
			return !member.getMemberno().equals(memberno);
		}
	}

	public boolean isEmailDuplicated(String email, Integer memberno) {
		if (memberno == null) {
			return mr.findByEmail(email).isPresent();
		} else {
			Optional<Member> memberOpt = mr.findByEmail(email);
			if (memberOpt.isEmpty()) {
				return false;
			}
			Member member = memberOpt.get();
			return !member.getMemberno().equals(memberno);
		}
	}

	public void modify(Integer memberno, String name, String cellnum, String birth, String email, String schoolName) {
		Member m = getMember(memberno);
		m.setName(name);
		m.setCellnum(cellnum);
		m.setBirth(birth);
		m.setEmail(email);
		m.setModDate(LocalDateTime.now());
		
		if(m.getRole().getValue() == 2) {
			m.setSchoolName(schoolName);
		}
		
		mr.save(m);
	}
	
	public void modifyPassword(Integer memberno, String pwd) {
	    // 입력 없으면 기존 비밀번호 유지 
	    if(pwd == null || pwd.isEmpty()) return;
	    
	    // getMember(Integer) 메서드를 활용하여 Optional 처리를 대체하고, 
	    // 회원이 없으면 이미 DataNotFoundException을 던지도록 함
	    Member member = getMember(memberno); 
	    
	    String encodedPwd = pe.encode(pwd); 
	    member.setPwd(encodedPwd);

	    mr.save(member);
	}
	public void remove(Member member) {
		mr.delete(member);
	}
	//중복 테스트
	public boolean checkMemberidDuplicate(String memberid) {
		return mr.existsByMemberid(memberid);
	}
	
	// 강사 등록 service (USER -> INSTRUCTOR)
	@Transactional
    public void updateRoleToTeacher(Integer memberno, String schoolName) {
        Member member = mr.findById(memberno)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        member.setRole(Role.INSTRUCTOR); // 강사 권한
        member.setSchoolName(schoolName); // 학교 이름 저장
    }
}