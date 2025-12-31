package com.project.lms;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.lms.apply.ApplyService;
import com.project.lms.attendance.AttendanceService;
import com.project.lms.course.Course;
import com.project.lms.course.CourseService;
import com.project.lms.member.Member;
import com.project.lms.member.MemberService;
import com.project.lms.member.Role;
import com.project.lms.progress.ProgressService;
import com.project.lms.question.Question;
import com.project.lms.question.QuestionService;
import com.project.lms.review.Review;
import com.project.lms.review.ReviewService;

import lombok.RequiredArgsConstructor; 


@RequiredArgsConstructor 
@Controller
public class MainController {
    private final MemberService memberService; 
	private final ApplyService applyService;
	private final AttendanceService attendanceService;
	private final CourseService courseService;
	private final ProgressService progressService;
	private final QuestionService questionService;
	private final ReviewService reviewService;
	
	@GetMapping("/")
	public String dashboard(Model model, Principal principal) {	    
	    
		List<Course> coursesToShow = Collections.emptyList(); // 비회원일 때 초기화
		model.addAttribute("coursesToShow", coursesToShow);    // 무조건 model에 추가
		
		if (principal != null) {
	        // 로그인한 member 정보 수집
	        Member member = memberService.getMember(principal.getName());
            model.addAttribute("member", member);
	        
         	//메인 페이지 출석일 출력
            long attendanceCount = attendanceService.getAttendanceCount(member);            
            model.addAttribute("attendanceCount", attendanceCount);
            
            // 권한 확인
            Role role = member.getRole();

            if (role == Role.USER) {
            	// 학생(사용자)용 데이터
            	// 수강 중인 강좌
                List<Course> enrolledCourses = applyService.getEnrolledCourses(member); 
                model.addAttribute("coursesToShow", enrolledCourses);
                
                // 수강 완료 강좌
                List<Integer> enrolledCourseIds = enrolledCourses.stream()
                        .map(Course::getCourseno)
                        .toList();
                
                List<Integer> completedCourseIds = progressService.getCompletedCourses(member.getMemberno(), enrolledCourseIds);

                // Course 객체로 변환
                List<Course> completedCourses = completedCourseIds.stream()
                        .map(courseService::getCourse) // courseService에서 단일 강좌 조회 필요
                        .toList();

                model.addAttribute("completedCourses", completedCourses);
            } else if (role == Role.INSTRUCTOR || role == Role.ADMIN) {
                // 강사, 관리자용 데이터
            	// 등록한 강좌
            	List<Course> instructorCourses = courseService.getCoursesByInstructor(member);
                model.addAttribute("coursesToShow", instructorCourses);
                
                // 질문 최신 3개
                List<Question> recentQuestions = questionService.getRecentQuestions(member.getMemberno(), 3);
                model.addAttribute("recentQuestions", recentQuestions);

                // 리뷰 최신 3개
                List<Review> recentReviews = reviewService.getRecentReviews(member.getMemberno(), 3);
                model.addAttribute("recentReviews", recentReviews);
            }
	    }
	    // 비회원 또는 회원 모두 동일한 뷰 반환 (단, 회원에게는 추가 데이터가 전달됨)
	    return "index"; 
	}
}