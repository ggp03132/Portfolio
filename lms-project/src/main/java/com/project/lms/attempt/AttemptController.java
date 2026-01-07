package com.project.lms.attempt;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.lms.member.Member;
import com.project.lms.member.MemberService;
import com.project.lms.quiz.Quiz;
import com.project.lms.quiz.QuizService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attempt")
@RequiredArgsConstructor
public class AttemptController {
	
	public final AttemptService attemptService;
	public final MemberService memberService;
	public final QuizService quizService;
	
	@GetMapping("/list")
	public String attemptList(Model model) {
		List<Attempt> attemptList = attemptService.getList();
		model.addAttribute("attemptList", attemptList);
		return "attemptListTest";
	}
	
	@GetMapping("/detail/{attemptno}")
	public String detail(@PathVariable("attemptno") Integer attemptno, Model model) {
		Attempt attempt = attemptService.getAttempt(attemptno);
		model.addAttribute("attempt", attempt);
		return "attemptDetailTest";
	}
	
	@PostMapping("/create/{quizno}")
	public String createAttempt(@PathVariable("quizno") Integer quizno, @RequestParam("score") Integer score, Principal principal, Model model) {
		Member member = memberService.getMember(principal.getName()); //로그인 사람 정보 가져오기
		Quiz quiz = quizService.getQuiz(quizno); //퀴즈 정보 가져오기
		attemptService.create(score, quiz, member); //저장
		model.addAttribute("quiz", quiz);
		model.addAttribute("score", score);
		return "attemptCreateTest";
	}
	
	@PostMapping("/delete/{attemptno}")
	public String attemptDelete(@PathVariable("attemptno") Integer attemptno) {
		Attempt attempt = attemptService.getAttempt(attemptno);
		attemptService.delete(attempt);
		return "";
	}
}