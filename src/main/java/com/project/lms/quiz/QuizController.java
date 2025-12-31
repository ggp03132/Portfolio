package com.project.lms.quiz;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
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
import com.project.lms.quizquestion.QuizQuestion;
import com.project.lms.useranswer.UserAnswerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {
	
	private final QuizService quizService;
	private final CourseService courseService;
	private final AttemptService attemptService;
	private final MemberService memberService;
	private final UserAnswerService userAnswerService;
	
	//퀴즈 리스트
	@GetMapping("/list/{courseno}")
	public String list(Model model, @PathVariable("courseno") Integer courseno) {
		Course course = courseService.getCourse(courseno);
		List<Quiz> quizList = quizService.getQuizByCourse(courseno);
		model.addAttribute("quizList", quizList);
		model.addAttribute("course", course);
		return "quiz/quiz_list";
	}
	
	//퀴즈 정보 상세조회
	@GetMapping("/detail/{quizno}")
	public String detail(Model model, @PathVariable("quizno") Integer quizno) {
		Quiz quiz = quizService.getQuiz(quizno);
		model.addAttribute("quiz", quiz);
		return "quiz/quiz_detail";
	}
	
	//퀴즈 정보 생성창 불러오기
	@GetMapping("/create/{courseno}")
	public String quizCreate(Model model, @PathVariable("courseno") Integer courseno, QuizForm quizForm) {
		Course course = courseService.getCourse(courseno);
		
		// 이미 퀴즈가 존재하는지 확인
		List<Quiz> existingQuizzes = quizService.getQuizByCourse(courseno);
		if (!existingQuizzes.isEmpty()) {
			model.addAttribute("courseno", courseno);
			model.addAttribute("errorMessage", "한 강좌당 하나의 퀴즈만 등록 가능합니다.");
			return "quiz/quiz_create_error";
		}
		
		model.addAttribute("course", course);
		// QuizForm은 Spring이 자동으로 빈 객체로 생성하여 모델에 추가합니다.
		return "quiz/quiz_create";
	}
	
	//퀴즈 정보 생성하기
	@PostMapping("/create/{courseno}")
	public String quizCreate(Model model, @PathVariable("courseno") Integer courseno, @Valid QuizForm quizForm, BindingResult bindingResult) {
		Course course = courseService.getCourse(courseno);
		
		// 이미 퀴즈가 존재하는지 확인
		List<Quiz> existingQuizzes = quizService.getQuizByCourse(courseno);
		if (!existingQuizzes.isEmpty()) {
			model.addAttribute("courseno", courseno);
			model.addAttribute("errorMessage", "한 강좌당 하나의 퀴즈만 등록 가능합니다.");
			return "quiz/quiz_create_error";
		}
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("course", course);
			return "quiz/quiz_create";
		}
		quizService.create(quizForm.getTitle(), quizForm.getTotalpoint(), course);
		return String.format("redirect:/quiz/list/%s", courseno);		
	}
	
	//퀴즈 정보 수정 폼을 보여주는 GET 매핑 수정
	@GetMapping("/modify/{quizno}")
	public String quizModify(Model model, @PathVariable("quizno") Integer quizno) {
	    Quiz quiz = quizService.getQuiz(quizno);

	    QuizForm quizForm = new QuizForm();
	    quizForm.setQuizno(quiz.getQuizno());           // 꼭 필요
	    quizForm.setTitle(quiz.getTitle());
	    quizForm.setTotalpoint(quiz.getTotalpoint());   // 꼭 필요

	    model.addAttribute("quizForm", quizForm);
	    model.addAttribute("quiz", quiz);
	    model.addAttribute("course", quiz.getCourse());

	    // 로깅 (디버깅용)
	    System.out.println("quizForm in GET -> " + quizForm);

	    return "quiz/quiz_modify";
	}

	
	@PostMapping("/modify/{quizno}")
	public String quizModify(Model model, @PathVariable("quizno") Integer quizno, @Valid QuizForm quizForm, BindingResult bindingResult) {
		Quiz quiz = quizService.getQuiz(quizno);
		Course course = quiz.getCourse();
		
		if(bindingResult.hasErrors()) {
			// 오류 발생 시, BindingResult와 함께 모델 객체들을 다시 템플릿에 전달
			model.addAttribute("quiz", quiz);
			model.addAttribute("course", course);
			return "quiz/quiz_modify";
		}
		
		quizService.modify(quiz, quizForm.getTitle(), quizForm.getTotalpoint());
		return String.format("redirect:/quiz/list/%s", quiz.getCourse().getCourseno());
	}
	
	//삭제
	@PostMapping("/delete/{quizno}")
	public String quizDelete(@PathVariable("quizno") Integer quizno) {
		Quiz quiz = quizService.getQuiz(quizno);
		quizService.delete(quiz);
		return String.format("redirect:/quiz/list/%s", quiz.getCourse().getCourseno());
	}
	
	//퀴즈풀기
	@GetMapping("/test/{quizno}")
	public String quizTest(@PathVariable("quizno") Integer quizno, Model model) {
		Quiz quiz = quizService.getQuiz(quizno);
		List<QuizQuestion> quizQuestion = quiz.getQuizquestionList();
		
		model.addAttribute("quiz", quiz);
		model.addAttribute("quizQuestion", quizQuestion);
		
		return "quiz/quizTest";
	}
	
	@PostMapping("/test/{quizno}") //MultiValueMap > 데이터 전체를 한번에 받기
	public String quizTest(@PathVariable("quizno") Integer quizno, Model model, @RequestParam MultiValueMap<String, String> formData, Principal principal) {
		
		Quiz quiz = quizService.getQuiz(quizno);
		List<QuizQuestion> questionList = quiz.getQuizquestionList();
		
		//점수 계산용 변수
		int totalScore = 0; //맞춘 총점
		int score = 0; //전체 점수 
		
		Member member = memberService.getMember(principal.getName()); //로그인 한 사람 정보

		
		for (QuizQuestion q : questionList) {
			String memberAnswer = formData.getFirst("answer"+q.getQuizquestionno());
			
			if(memberAnswer != null && memberAnswer.equalsIgnoreCase(q.getAnswer())) { //대소문자 구분 없이 정답 비교
				totalScore += q.getPoint(); //맞춘 점수 + 문제 배점
			}
			score += q.getPoint(); //총점 계산. 총점 + 배점
		}
		
		Attempt attempt = attemptService.create(totalScore, quiz, member); // 점수 저장
		
		//사용자 응답에 저장
	    for (QuizQuestion q : questionList) {
	        String memberAnswer = formData.getFirst("answer" + q.getQuizquestionno());
	        boolean isCorrect = (memberAnswer != null && memberAnswer.equalsIgnoreCase(q.getAnswer()));
	        userAnswerService.create(attempt, q, memberAnswer, isCorrect); //<< 저장
	    }
		
		model.addAttribute("quiz", quiz);
		model.addAttribute("totalScore", totalScore);
		model.addAttribute("score", score);
		model.addAttribute("questionList", questionList);
		
		return "quiz/quizResultTest";
	}
	
}
