package com.project.lms.answer;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.project.lms.member.Member;
import com.project.lms.member.MemberService;
import com.project.lms.question.Question;
import com.project.lms.question.QuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {
	
	private final AnswerService answerService;
	private final QuestionService questionService;
	private final MemberService memberService;
	
	@PostMapping("/create/{questionno}")
	public String create(Model model, @PathVariable("questionno") Integer questionno, @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
		Question question = questionService.getQuestion(questionno);
		Member member = memberService.getMember(principal.getName());
		if(bindingResult.hasErrors()) {
			model.addAttribute("question", question);
			return "question/questionDetailTest";
		}
		answerService.create(question, answerForm.getContent(), member);
		return "redirect:/question/detail/"+questionno;
	}
	
	@PostMapping("/modify/{answerno}")
	public String modify(@PathVariable("answerno") Integer answerno, @RequestParam("content") String content, Principal principal) {
		Answer answer = answerService.getAnswer(answerno);
		
		String loginMember = principal.getName();
		Member member = memberService.getMember(loginMember);
		boolean isINSTRUCTOR = member.getRole().name().equals("INSTRUCTOR");
		boolean isMember = answer.getMember().getName().equals(loginMember);
		
		if(!isINSTRUCTOR && !isMember) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		
		answerService.modify(answer, content);
		Integer questionno = answer.getQuestion().getQuestionno();
		return "redirect:/question/detail/"+questionno;
	}
	
	@PostMapping("/delete/{answerno}")
	public String delete(@PathVariable("answerno") Integer answerno, Principal principal) {
		Answer answer = answerService.getAnswer(answerno);
		
		String loginMember = principal.getName();
		Member member = memberService.getMember(loginMember);
		boolean isAdmin = member.getRole().name().equals("ADMIN");
		boolean isMember = answer.getMember().getMemberid().equals(loginMember);
		
		if(!isAdmin && !isMember) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		
		
		answerService.delete(answer);
		return "redirect:/question/detail/"+answer.getQuestion().getQuestionno();
	}
}