package com.project.lms.question;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.project.lms.answer.AnswerForm;
import com.project.lms.course.Course;
import com.project.lms.course.CourseService;
import com.project.lms.lesson.Lesson;
import com.project.lms.lesson.LessonService;
import com.project.lms.member.Member;
import com.project.lms.member.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {
	
	private final QuestionService questionService;
	private final MemberService memberService;
	private final LessonService lessonService;
	private final CourseService courseService;
	
	@GetMapping("/list")
	public String list(Model model, @RequestParam(value="page", defaultValue = "0") int page) {
		Page<Question> paging = questionService.getList(page);
		model.addAttribute("paging", paging);
		return "question/questionListTest";
	}
	
	@GetMapping("/detail/{questionno}")
	public String detail(Model model, @PathVariable("questionno") Integer questionno, AnswerForm answerForm) {
		Question question = questionService.getQuestion(questionno);
		model.addAttribute("question", question);
		
		Course course = null;
		 
		if(question.getLesson() != null) {
			course = question.getLesson().getCourse();
		} else {
			course = question.getCourse();
		}
		
	    model.addAttribute("course", course);
		return "question/questionDetailTest";
	}
	
	//등록폼 
	@GetMapping("/create/{courseno}")
	public String questionCreate(QuestionForm questionForm, Model model, @PathVariable("courseno") Integer courseno) { //강좌에 속한 강의를 불러오기 위해 courseno 추가
		Course course = courseService.getCourse(courseno); //강좌no 불러오기
		List<Lesson> lessonList = lessonService.getLessonByCourse(course); //강좌 속 강의 리스트 가져오기
		model.addAttribute("course", course);
		model.addAttribute("lessonList", lessonList);
		return "question/questionCreateTest";
	}
	
	//등록
	@PostMapping("/create/{courseno}")
	public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal, Model model, @PathVariable("courseno") Integer courseno) {
		if(bindingResult.hasErrors()) {
			Course course = courseService.getCourse(questionForm.getCourseno());
			model.addAttribute("course", course);
			model.addAttribute("lessonList", lessonService.getLessonByCourse(course));
			return "question/questionCreateTest";
		}
		Member member = memberService.getMember(principal.getName());
		Course course = courseService.getCourse(courseno);
//		Lesson lesson = lessonService.getLesson(questionForm.getLessonno()); 강의 선택 필수 일때 이 코드 사용
		
		//강의 선택 안해도 질문 가능하게 하기
		Lesson lesson = null;
		if(questionForm.getLessonno() != null) {
			lesson = lessonService.getLesson(questionForm.getLessonno());
		}
		
		questionService.create(questionForm.getTitle(), questionForm.getContent(), member, lesson, course);
		
		if(lesson != null) {
			Integer courseNo = lesson.getCourse().getCourseno(); //등록 후 courseno 디테일로 보내기
			return "redirect:/course/detail/"+courseNo;
		} else {
			return "redirect:/course/detail/"+questionForm.getCourseno(); 
		}
	}
	
	//수정
	@GetMapping("/modify/{questionno}")
	public String questionModify(QuestionForm questionForm, @PathVariable("questionno") Integer questionno, Principal principal) {
		Question question = questionService.getQuestion(questionno);
		
		String loginMember = principal.getName();
		Member member = memberService.getMember(loginMember);
		boolean isAdmin = member.getRole().name().equals("ADMIN");
		boolean isMember = question.getMember().getMemberid().equals(loginMember);
		
		if(!isAdmin && !isMember) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		questionForm.setTitle(question.getTitle());
		questionForm.setContent(question.getContent());
		return "question/questionModifyTest";
	}
	
	@PostMapping("/modify/{questionno}")
	public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal, @PathVariable("questionno") Integer questionno) {
		if(bindingResult.hasErrors()) {
			return "question/questionModifyTest";
		}
		Question question = questionService.getQuestion(questionno);
		
		String loginMember = principal.getName();
		Member member = memberService.getMember(loginMember);
		boolean isAdmin = member.getRole().name().equals("ADMIN");
		boolean isMember = question.getMember().getMemberid().equals(loginMember);
		
		if(!isAdmin && !isMember) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		questionService.modify(question, questionForm.getTitle(), questionForm.getContent());
		return String.format("redirect:/question/detail/%s", questionno);
	}
	
	//삭제
	@PostMapping("/delete/{questionno}")
	public String delete(Principal principal, @PathVariable("questionno") Integer questionno) {
		Question question = questionService.getQuestion(questionno);
		
		String loginMember = principal.getName();
		Member member = memberService.getMember(loginMember);
		boolean isAdmin = member.getRole().name().equals("ADMIN");
		boolean isMember = question.getMember().getMemberid().equals(loginMember);
		
		if(!isAdmin && !isMember) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		questionService.delete(question);
		
		Integer courseno;
		if(question.getLesson() != null) {
			courseno = question.getLesson().getCourse().getCourseno();
		} else {
			courseno = question.getCourse().getCourseno();
		}
		
		return "redirect:/course/detail/"+courseno;
	}
}