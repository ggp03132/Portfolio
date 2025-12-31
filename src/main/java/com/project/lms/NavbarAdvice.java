package com.project.lms;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.project.lms.apply.Apply;
import com.project.lms.apply.ApplyService;
import com.project.lms.member.Member;
import com.project.lms.member.MemberService;

//모든 컨트롤러의 view에 공통적으로 적용함. 이 컨트롤러가 하는 역할을 네비바에 구현된 드롭다운 메뉴를
//모든 페이지에서 동작하기 위함
@ControllerAdvice
public class NavbarAdvice {
    @Autowired
    private MemberService memberService;
    @Autowired
    private ApplyService applyService;
    
    // @ModelAttribute = 컨트롤러 메서드의 반환값을 뷰에 모델 속성으로 자동 추가
 	// "applyList"라는 이름으로 뷰에 전달
    @ModelAttribute("applyList")
    // view에 사용할 로그인한 회원의 수강등록 목록
    public List<Apply> populateNavbarApplyList(Principal principal) {
        // 로그인하지 않은 경우(principal이 null) = null 반환.
        if (principal == null) return null;
        // 로그인한 회원의 ID를 가져와서 DB에 조회
        Member member = memberService.getMember(principal.getName());
        // 조회된 member 객체를 기반으로 applyService에서 해당 회원의 Apply 리스트를 가져옴.
        return applyService.getApplyByMember(member);
    }
}