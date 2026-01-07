package com.project.lms.lesson;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.lms.course.Course;
import com.project.lms.course.CourseService;
import com.project.lms.member.Member;
import com.project.lms.member.MemberService;
import com.project.lms.progress.ProgressService;
import com.project.lms.quiz.Quiz;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lesson")
public class LessonController {
	private final LessonService lessonService;
	private final CourseService courseService;
	private final MemberService memberService;
	private final ProgressService progressService;


	//하나 조회
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/detail/{lessonno}")
	public String detail(Model model, 
	                     @PathVariable("lessonno") Integer lessonno,
	                     Principal principal) {   
	    Lesson lesson = lessonService.getLesson(lessonno);
	    model.addAttribute("lesson", lesson);

	    Course course = lesson.getCourse();
	    model.addAttribute("course", course);
	    model.addAttribute("lessonList", course.getLessonList());
	    
	    //lesson/detail 창에서 퀴즈 풀기 띄울거니까 model에 담을 quiz 추가
	    Quiz quiz = lesson.getCourse().getQuiz();
	    model.addAttribute("quiz", quiz);
	    
	    // 로그인한 회원의 수강 진행도 조회
	    Member member = memberService.getMember(principal.getName());
	    List<Integer> completedLessons = progressService.getCompletedLessonIds(
	            member.getMemberno(), course.getCourseno()
	    );
	    model.addAttribute("completedLessons", completedLessons != null ? completedLessons : List.of());

	    // 이전/다음 강의 번호 계산
	    List<Lesson> lessonList = course.getLessonList(); 
	    int currentIdx = -1;
	    for (int i = 0; i < lessonList.size(); i++) {
	        if (lessonList.get(i).getLessonno().equals(lessonno)) {
	            currentIdx = i;
	            break;
	        }
	    }

	    Integer prevLessonNo = (currentIdx > 0) ? lessonList.get(currentIdx - 1).getLessonno() : null;
	    Integer nextLessonNo = (currentIdx < lessonList.size() - 1) ? lessonList.get(currentIdx + 1).getLessonno() : null;

	    model.addAttribute("prevLessonNo", prevLessonNo);
	    model.addAttribute("nextLessonNo", nextLessonNo);

	    return "lesson/lesson_detail";
	}

	
	//생성 정보 불러오기
	@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
	@GetMapping("/create/{courseno}")
	public String lessonCreate(Model model, 
							   @PathVariable("courseno") Integer courseno, 
							   LessonForm lessonForm,
							   Principal principal) {
		Course course = courseService.getCourse(courseno);
		
		// 로그인한 사람의 id를 가져오는 loginMember, 강사가 null이거나 강좌의 강사 id가 같지 않으면 리스트로 리다이렉트
		String loginMember = principal.getName();
		Member member = memberService.getMember(loginMember);
		boolean isAdmin = member.getRole().name().equals("ADMIN");
		
        if(course.getInstructor() == null || !course.getInstructor().getMemberid().equals(loginMember)) {
        	if(!isAdmin) {
        	return "redirect:/course/list";
        	}
        }
        
		model.addAttribute("course", course);
		return "lesson/lesson_create";
	}
	
	//생성하기
	@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
	@PostMapping("/create/{courseno}")
	public String lessonCreate(Model model, 
							   @PathVariable("courseno") Integer courseno, 
							   @Valid LessonForm lessonForm, 
							   BindingResult bindingResult) {
		Course course = courseService.getCourse(courseno);
		if(bindingResult.hasErrors()) {
			model.addAttribute("course", course);
			return "lesson/lesson_create";
		}
		lessonService.create(lessonForm.getTitle(), lessonForm.getContent(), lessonForm.getVideo(), lessonForm.getTime(), course);
		return String.format("redirect:/course/detail/%s", courseno);
	}
	
	//수정 getmapping (조회)
	@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
	@GetMapping("/modify/{lessonno}")
	public String lessonModify(Model model, 
							   @PathVariable("lessonno") Integer lessonno,
							   Principal principal) {
		Lesson lesson = lessonService.getLesson(lessonno);
		
		// 로그인한 사람의 id를 가져오는 loginMember, 강사가 null이거나 강의 정보의 강좌의 강사 id가 같지 않으면 리스트로 리다이렉트       
		String loginMember = principal.getName();
		Member member = memberService.getMember(loginMember);
		boolean isAdmin = member.getRole().name().equals("ADMIN");
		
		if(lesson.getCourse().getInstructor() == null || !lesson.getCourse().getInstructor().getMemberid().equals(loginMember)) {
			if(!isAdmin) {
			return "redirect:/course/list";
			}
		}
		
		LessonForm lessonForm = new LessonForm();
		
		lessonForm.setLessonno(lesson.getLessonno());
		lessonForm.setTitle(lesson.getTitle());
		lessonForm.setContent(lesson.getContent());
		lessonForm.setVideo(lesson.getVideo());
		lessonForm.setTime(lesson.getTime());
		model.addAttribute("lessonForm", lessonForm);
		model.addAttribute("lesson", lesson);
		return "lesson/lesson_modify";
	}
	
	//수정 입력 (post)
	@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
	@PostMapping("/modify/{lessonno}")
	public String lessonModify(Model model, 
							   @PathVariable("lessonno") Integer lessonno, 
							   @Valid LessonForm lessonForm, 
							   BindingResult bindingResult) {
		Lesson lesson = lessonService.getLesson(lessonno);
		if(bindingResult.hasErrors()) {
			model.addAttribute("lesson", lesson);
			return "lesson/lesson_modify";
		}
		lessonService.modify(lesson, 
							 lessonForm.getTitle(), 
							 lessonForm.getContent(), 
							 lessonForm.getVideo(), 
							 lessonForm.getTime());
		Integer courseno = lesson.getCourse().getCourseno();
		return "redirect:/course/detail/"+ courseno;
	}
	
	//삭제
	@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
	@PostMapping("/delete/{lessonno}")
	public String lessonDelete(@PathVariable("lessonno") Integer lessonno) {
		Lesson lesson = lessonService.getLesson(lessonno);
		lessonService.delete(lesson);
		
		Integer courseno = lesson.getCourse().getCourseno();

		return "redirect:/course/detail/"+ courseno;
	}
	
	//강의 진행도
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/complete/{lessonno}")
	@ResponseBody
	public ResponseEntity<?> completeLesson(@PathVariable("lessonno") Integer lessonno,
	                                        Principal principal) {
	    String loginId = principal.getName();
	    Member member = memberService.getMember(loginId);
	    Integer memberId = member.getMemberno();

	    progressService.completeLesson(memberId, lessonno);

	    Integer courseId = progressService.getCourseIdFromLesson(lessonno);
	    double progress = progressService.getCourseProgress(memberId, courseId);
	    
	    // 사용자가 완료한 lesson 목록
	    List<Integer> completedLessons = progressService.getCompletedLessonIds(memberId, courseId);

	    return ResponseEntity.ok(Map.of(
	        "message", "강의를 완료했습니다!",
	        "progress", progress,
	        "completedLessons", completedLessons
	    ));
	}


}
