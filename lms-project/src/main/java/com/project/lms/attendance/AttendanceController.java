package com.project.lms.attendance;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.lms.DataNotFoundException;
import com.project.lms.member.Member;
import com.project.lms.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {
	
	private final AttendanceService attendanceService;
	private final MemberRepository memberRepository;
	
	//출석체크
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") //일반 회원만
    @PostMapping("/check")
    public String checkAttend(RedirectAttributes redirectAttributes, Principal principal) { //RedirectAttributes이거는 리다이렉트 할 때 데이터 전송, model은 리다이렉트 후 데이터 전송이 안됨
    	
    	Member member = memberRepository.findByMemberid(principal.getName()) //멤버 리포지터리의 멤버아이디를 로그인한 사람으로 찾음
    			.orElseThrow(() -> new DataNotFoundException("회원 정보가 없습니다"));
    	
    	try {
    		attendanceService.checkAttend(member); //서비스의 checkAttend가 멤버 타입이라 위에도 Member member = ...
    		redirectAttributes.addFlashAttribute("message", "출석 체크 완료!! +10p"); //여기 이 message는 html에 ${message}에 나옴
    	} catch(DataNotFoundException e) {
    		redirectAttributes.addFlashAttribute("message", e.getMessage());
    	}
    	return "redirect:/";
    }
   
}