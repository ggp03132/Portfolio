package com.project.lms.member;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.lms.attendance.AttendanceService;
import com.project.lms.course.Course;
import com.project.lms.course.CourseRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
	private final MemberService ms;
	private final AttendanceService attendanceService;
	private final CourseRepository courseRepository;

	//list
	@GetMapping("/list")
	public String list(Model model, 
			@RequestParam(value = "page", defaultValue = "0") int page, 
			@RequestParam(value = "role", required = false) String roleValue,
			@RequestParam(value = "keyword", required = false) String keyword) {
		Page<Member> pm;
		
		// 검색어가 있는 경우
		if (keyword != null && !keyword.trim().isEmpty()) {
			if (roleValue != null && !roleValue.isEmpty()) {
				// 역할 필터와 검색어 모두 적용
				Role role = Role.fromValue(Integer.parseInt(roleValue));
				pm = ms.searchMembersByRole(role, keyword, page);
			} else {
				// 검색어만 적용
				pm = ms.searchMembers(keyword, page);
			}
		} else {
			// 검색어가 없는 경우 기존 로직
			if (roleValue != null && !roleValue.isEmpty()) {
				Role role = Role.fromValue(Integer.parseInt(roleValue));
				pm = ms.getListByRole(role, page);
			} else {
				pm = ms.getList(page);
			}
		}
		
		model.addAttribute("pm", pm);
		model.addAttribute("selectedRole", roleValue);
		model.addAttribute("keyword", keyword);
		return "member/member_list";
	}
	
	// detail
		@PreAuthorize("isAuthenticated()")
		@GetMapping("/detail/{memberno}")//경로 변경
		public String memberDetail(Model model, @PathVariable("memberno") Integer memberno, Principal principal) { 
		    
		    // 1. 상세 조회 대상 회원 정보 가져오기
		    Member targetMember = ms.getMember(memberno);
		    
		    // 2. 현재 로그인 사용자 정보 가져오기
		    String currentMemberid = principal.getName();
		    Member currentUser = ms.getMember(currentMemberid);
		    
		    // 3. 접근 권한 확인: 본인이거나 현재 사용자가 관리자 역할이어야 조회 가능
		    if (!targetMember.getMemberid().equals(currentMemberid) && currentUser.getRole() != Role.ADMIN) { 
		        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
		    }
		    // 4. 출석일 카운트 
	        // 출석일 수 조회 
	        int attendanceCount = attendanceService.getAttendanceCount(targetMember);
	        
	        // 5. Model에 출석일 저장
	        model.addAttribute("attendanceCount", attendanceCount);
		    model.addAttribute("member", targetMember); // 상세 조회할 회원 정보를 모델에 담아 전달
		    return "member/member_detail";
		}
		
	//create	
	@GetMapping("/create")
	public String memberCreate(Model model) {
		model.addAttribute("memberForm", new MemberForm());
		return "member/member_form";
	}

	@PostMapping("/create")
	public String memberCreate(@Valid MemberForm memberForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {

			model.addAttribute("memberForm", memberForm);

			return "member/member_form";
		}
		if (memberForm.getPwd() == null || memberForm.getPwd().isEmpty()) {
			bindingResult.rejectValue("pwd", "required", "비밀번호는 필수입니다.");
		}
		if (memberForm.getPwd2() == null || memberForm.getPwd2().isEmpty()) {
			bindingResult.rejectValue("pwd2", "required", "비밀번호 확인은 필수입니다.");
		}

		// pwd,pwd2 일치 여부 확인
		if (!memberForm.getPwd().equals(memberForm.getPwd2())) {
			bindingResult.rejectValue("pwd2", "mismatch", "비밀번호가 일치하지 않습니다.");
			return "member/member_form";
		}
		if (ms.isIdDuplicated(memberForm.getMemberid(), memberForm.getMemberno())) {
			bindingResult.rejectValue("memberid", "duplicate", "이미 사용 중인 아이디입니다.");
			return "member/member_form";
		}
		if (ms.isCellnumDuplicated(memberForm.getCellnum(), memberForm.getMemberno())) {
			bindingResult.rejectValue("cellnum", "duplicate", "이미 사용 중인 휴대폰 번호입니다.");
			return "member/member_form";
		}
		if (ms.isEmailDuplicated(memberForm.getEmail(), memberForm.getMemberno())) {
			bindingResult.rejectValue("email", "duplicate", "이미 사용 중인 이메일입니다.");
			return "member/member_form";
		} else {
			ms.create(memberForm.getMemberid(), memberForm.getPwd(), memberForm.getName(), memberForm.getCellnum(),
					memberForm.getBirth(), memberForm.getEmail());
		}
		
		redirectAttributes.addFlashAttribute("msg", "회원가입이 완료되었습니다. 로그인해주세요!");

		return "redirect:/member/login";
	}
	
	 //아이디 중복 체크 테스트
	/* ResponseEntity<> : HTTP 응답 전체를 감싸서 반환할 수 있는 객체 (중복테스트는 boolean을 담음, true면 중복 false면 중복 x)
	 * ResponseEntity.ok() : 성공 상태 (http 200)와 함께 반환값을 보냄 */
	@GetMapping("/check_id")
	public ResponseEntity<Boolean> checkMemberIdDuplicate(@RequestParam("memberid") String memberid){
		return ResponseEntity.ok(ms.checkMemberidDuplicate(memberid));
	}	
	
	// 마이페이지용 
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/detail")
	public String memberDetailRedirect(Principal principal) {
	    // 현재 로그인된 사용자(본인)의 Member 객체를 가져옴
	    Member member = ms.getMember(principal.getName());
	    
	    // /member/detail/{memberno} 경로로 리다이렉트
	    return "redirect:/member/detail/" + member.getMemberno();
	}
	
	//modify (GET)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{memberno}")
	public String memberModify(Model model, MemberForm memberForm, @PathVariable("memberno") Integer memberno,
	        Principal principal) {

	    Member member = ms.getMember(memberno);
	    
	    // 현재 로그인 사용자 정보
	    Member current = ms.getMember(principal.getName());
	    
	    // 관리자가 아닌 다른 사용자 일때 권한 x
	    if (!member.getMemberid().equals(principal.getName()) && current.getRole() != Role.ADMIN) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
	    }
	    
	    boolean isAdmin = current.getRole() == Role.ADMIN; // Role.ADMIN과 비교

	    memberForm.setMemberid(member.getMemberid());
	    memberForm.setPwd(member.getPwd());
	    memberForm.setName(member.getName());
	    memberForm.setCellnum(member.getCellnum());
	    memberForm.setBirth(member.getBirth());
	    memberForm.setEmail(member.getEmail());

	    model.addAttribute("관리자", isAdmin);
	    model.addAttribute("memberno", memberno);

	    return "member/member_modify";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{memberno}")
	public String memberModify(@Valid MemberForm memberForm, BindingResult bindingResult,
	        @PathVariable("memberno") Integer memberno, Principal principal, Model model) {

	    // 수정 대상 및 현재 사용자 정보 조회
	    Member member = ms.getMember(memberno); 
	    String currentMemberid = principal.getName();
	    Member currentUser = ms.getMember(currentMemberid); 
	    
	    // 관리자가 아닌 다른 사용자 일때 권한 x
	    if (!member.getMemberid().equals(currentMemberid) && currentUser.getRole() != Role.ADMIN) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
	    }
	    
	    // 비밀번호 변경 요청 처리
	    if (memberForm.getPwd() != null && !memberForm.getPwd().isEmpty()) {
	        // pwd,pwd2 일치여부확인
	        if (!memberForm.getPwd().equals(memberForm.getPwd2())) {
	            bindingResult.rejectValue("pwd2", "mismatch", "비밀번호가 일치하지 않습니다.");
	            return "member/member_modify";
	        }
	        // 새로운 비밀번호 업데이트는 별도 서비스 메서드로 처리
	        ms.modifyPassword(memberno, memberForm.getPwd());
	    }


	    //  일반 정보 중복 및 유효성 검사 (유지)
	    if (ms.isIdDuplicated(memberForm.getMemberid(), memberForm.getMemberno())) {
	        bindingResult.rejectValue("memberid", "duplicate", "이미 사용 중인 아이디입니다.");
	        return "member/member_modify";
	    }
	    if (ms.isCellnumDuplicated(memberForm.getCellnum(), memberForm.getMemberno())) {
	        bindingResult.rejectValue("cellnum", "duplicate", "이미 사용 중인 휴대폰 번호입니다.");
	        return "member/member_modify";
	    }
	    if (ms.isEmailDuplicated(memberForm.getEmail(), memberForm.getMemberno())) {
	        bindingResult.rejectValue("email", "duplicate", "이미 사용 중인 이메일입니다.");
	        return "member/member_modify";
	    }

	    //  일반 정보 업데이트 (Role과 Pwd를 제외한 필드만 서비스에 전달)
	    ms.modify(memberno, memberForm.getName(), memberForm.getCellnum(), memberForm.getBirth(), memberForm.getEmail(), memberForm.getSchoolName());

	    if (member.getMemberid().equals(currentMemberid)) {
	        // 본인이 수정했을 경우: 마이페이지 경로로 리다이렉트 
	        return "redirect:/member/detail";
	    } else {
	        // 관리자가 다른 사람을 수정했을 경우: 수정된 회원의 상세 페이지로 리다이렉트
	        return "redirect:/member/detail/" + memberno;
	    }
	}
	
	//delete	
	@PreAuthorize("isAuthenticated()")  
	@GetMapping("/delete/{memberno}")
	public String memberDelete(@PathVariable("memberno") Integer memberno,Principal principal,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
	    
		// 1. 삭제 대상 회원 조회
		Member member = ms.getMember(memberno);
		
		// 2. 현재 로그인 사용자 조회
	    String currentMemberid = principal.getName();
		Member currentUser = ms.getMember(currentMemberid);
	    // 3. 관리자가 아닌 다른 사용자일때는 권한 x
		if(!member.getMemberid().equals(currentMemberid) && currentUser.getRole() != Role.ADMIN) { 
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,"삭제 권한이 없습니다.");
		}	  
		
		// 강사가 회원탈퇴 할떄
		if (member.getRole() == Role.INSTRUCTOR) {
	        List<Course> courseList = courseRepository.findByInstructor(member);

	        if (courseList != null && !courseList.isEmpty()) {
	            // 강좌가 존재할 경우 예외 처리 
	        	redirectAttributes.addFlashAttribute("errorMessage",
	                    "등록된 강좌가 존재하여 탈퇴할 수 없습니다.");
	                return "redirect:/member/detail/{memberno}";
	        }
	    }
	    // 4. 삭제 처리
		ms.remove(member);
	    
	    // 5. 삭제 후 처리
	    if (member.getMemberid().equals(currentMemberid)) {
	        try {
	            request.logout();
	        } catch (ServletException e) {
	            e.printStackTrace();
	        }
	        return "redirect:/";
	    } else {
	        // 관리자가 다른 회원을 삭제한 경우 목록 페이지로 리다이렉트
	        return "redirect:/member/list"; 
	    }
	}

	//login,logout
	@GetMapping("/login")
	public String login() {
		return "member/member_login";
	}

	@GetMapping("/logout")
	public String logout() {
		return "redirect:/";
	}
	
	// 강사 등록 팝업창 호출
	@GetMapping("/role/form/{memberno}")
	public String showRoleForm(@PathVariable("memberno") Integer memberno, Model model) {
	    model.addAttribute("memberno", memberno);
	    return "role_form"; // 위의 role_form.html 경로
	}
	
	// 학교 이름 입력 후 DB에 권한 변경 처리
	@PostMapping("/role/edit/{memberno}")
	@ResponseBody
	public ResponseEntity<?> updateRoleToTeacher(
	        @PathVariable("memberno") Integer memberno,
	        @RequestBody Map<String, String> body,
	        HttpServletRequest request) {
	    String schoolName = body.get("schoolName");
	    ms.updateRoleToTeacher(memberno, schoolName);
	
	    return ResponseEntity.ok().build();
	}
}