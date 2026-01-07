package com.project.lms.useranswer;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.lms.attempt.Attempt;
import com.project.lms.attempt.AttemptService;
import com.project.lms.course.Course;
import com.project.lms.course.CourseService;
import com.project.lms.member.Member;
import com.project.lms.member.MemberService;
import com.project.lms.quiz.Quiz;
import com.project.lms.quiz.QuizService;
import com.project.lms.quizquestion.QuizQuestion;
import com.project.lms.quizquestion.QuizQuestionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/useranswer")
public class UserAnswerController {
	
	private final UserAnswerService userAnswerService;
	private final AttemptService attemptService;
	private final QuizQuestionService quizQuestionService;
	private final MemberService memberService;
	private final QuizService quizService;
	private final CourseService courseService;
	
	//강사, 관리자 확인용 > 학생 퀴즈 점수
	@GetMapping("/attempt/{courseno}")
	public String useranswerQuizAttempt(@PathVariable("courseno") Integer courseno, Model model) {
		
		// 1. 코스 정보
	    Course course = courseService.getCourse(courseno);
	    model.addAttribute("course", course);
	    
	    // 2. 코스의 모든 퀴즈
	    List<Quiz> quizList = quizService.getQuizByCourse(courseno);
	    model.addAttribute("quizList", quizList);

	    // 3. 모든 퀴즈에 대한 학생 답안
	    if (!quizList.isEmpty()) {
	        List<UserAnswer> useranswerList = userAnswerService.getAnswerByCourse(courseno);
	        model.addAttribute("useranswerList", useranswerList);
	    } else {
	        model.addAttribute("message", "등록된 퀴즈가 없습니다.");
	    }
	    
		return "uaQuizAttemptTest";
	}
	
	@GetMapping("/list/{attemptno}")
	public String useranswerList(@PathVariable("attemptno") Integer attemptno, Model model) {
		Attempt attempt = attemptService.getAttempt(attemptno);
		List<UserAnswer> useranswerList = userAnswerService.getListByAttempt(attempt);
		model.addAttribute("attempt", attempt);
		model.addAttribute("useranswerList", useranswerList);
		return "uaListTest";
	}
	
	@PostMapping("/create/{quizQuestionno}/{attemptno}")
	public String createAnswer(@PathVariable("quizQuestionno") Integer quizQuestionno,
			@PathVariable("attemptno") Integer attemptno,
			@RequestParam("answer") String answer,
			Principal principal, Model model) {
		
		Member member = memberService.getMember(principal.getName());
		Attempt attempt = attemptService.getAttempt(attemptno);
		QuizQuestion quizQuestion = quizQuestionService.getQuestion(quizQuestionno);
		
		Boolean isCorrect = quizQuestion.getAnswer().equals(answer);
		
		userAnswerService.create(attempt, quizQuestion, answer, isCorrect);
		
		model.addAttribute("attempt", attempt);
		model.addAttribute("quizQuestion", quizQuestion);
		model.addAttribute("answer", answer);
		model.addAttribute("isCorrect", isCorrect);
		model.addAttribute("member", member);
		return "uaCreateTest";
	}
}
