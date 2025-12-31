package com.project.lms.memo;

import java.security.Principal;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.project.lms.lesson.Lesson;
import com.project.lms.lesson.LessonService;
import com.project.lms.member.Member;
import com.project.lms.member.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/memo")
public class MemoController {

	private final MemoService memoService;
	private final MemberService memberService;
	private final LessonService lessonService;

	// 리스트
	@GetMapping("/list")
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, Principal principal) {
		Member member = memberService.getMember(principal.getName()); // 본인 로그인

		Page<Memo> pm = memoService.getMemoByMember(page, member);
		model.addAttribute("pm", pm); // 본인 메모 가져오기

		return "memo/memo_list";
	}

	// 강의를 듣는 중 메모 저장 기능 추가(JSON을 받기 위한 AJAX 요청 방식)
	@PostMapping("/create-ajax/{lessonno}")
	@ResponseBody
	public Map<String, Object> createMemoAjax(
	        @PathVariable("lessonno") Integer lessonno,
	        @RequestBody Map<String, String> request,
	        Principal principal) {

	    String content = request.get("content");
	    
	    Member member = memberService.getMember(principal.getName());
	    Lesson lesson = lessonService.getLesson(lessonno);

	    memoService.Memocreate(content, lesson, member);

	    return Map.of("result", "success", "message", "메모 저장 완료");
	}

//	//상세
//	@GetMapping("/detail/{memono}")
//	public String detail() {
//		return "memodetailtest";
//	}

	// 수정
	@GetMapping("/modify/{memono}")
	public String ModifyMemo(MemoForm memoform, @PathVariable("memono") Integer memono, Principal principal,
			Model model) {
		Memo memo = memoService.getMemo(memono);

		String loginMember = principal.getName();
		Member member = memberService.getMember(principal.getName()); // 본인 로그인

		boolean isMember = memo.getMember().getName().equals(loginMember);

		memoform.setContent(memo.getContent());

		model.addAttribute("memono", memono);
		model.addAttribute("memo", memo);
		model.addAttribute("memoForm", memoform);

		return "memo/memo_modify";
	}

	@PostMapping("/modify/{memono}")
	public String ModifyMemo(@Valid MemoForm memoForm,
							BindingResult bindingResult,
							Model model,
							@PathVariable("memono") Integer memono, 
							Principal principal) {
		if (bindingResult.hasErrors()) {
			return "memo_modify";
		}
		Memo memo = memoService.getMemo(memono);

		String loginMember = principal.getName();
		Member member = memberService.getMember(principal.getName()); // 본인 로그인

		boolean isMember = memo.getMember().getName().equals(loginMember);
		
		memoService.modify(memono,memoForm.getContent());
		return "redirect:/memo/list";
	}

	// 삭제
	@GetMapping("/delete/{memono}")
	public String memoDelete(@PathVariable("memono") Integer memono, 
							 Principal principal) {
		Memo memo = memoService.getMemo(memono);

		String loginMember = principal.getName();
		Member member = memberService.getMember(principal.getName()); // 본인 로그인

		boolean isMember = memo.getMember().getName().equals(loginMember);
		
		memoService.delete(memo);
		return "redirect:/memo/list";
	}

}
