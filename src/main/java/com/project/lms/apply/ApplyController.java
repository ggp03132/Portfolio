package com.project.lms.apply;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.lms.DataNotFoundException;
import com.project.lms.attempt.AttemptService;
import com.project.lms.course.Course;
import com.project.lms.course.CourseService;
import com.project.lms.member.Member;
import com.project.lms.member.MemberService;
import com.project.lms.progress.ProgressService;
import com.project.lms.quiz.Quiz;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/apply")
public class ApplyController {
	
	private final ApplyService applyService;
	private final MemberService memberService;
	private final CourseService courseService;
	private final ProgressService progressService;
	private final AttemptService attemptService;
	
	//강좌 신청
	@PostMapping("/course/{courseno}")
	public String ApplyCourse(@PathVariable("courseno") Integer courseno, Principal principal) {
		Member member = memberService.getMember(principal.getName());
		Course course = courseService.getCourse(courseno);
		
		try {
			applyService.enroll(member, course);
			System.out.println("수강신청완료"); //에러 나서 콘솔창 확인용
		} catch(IllegalStateException e) {
			System.out.println("예외 발생 : "+e.getMessage());
			return "redirect:/course/detail/"+courseno+"?forbidden";
		} catch (DataNotFoundException e) {
			System.out.println("예외 발생 : "+e.getMessage());
			return "redirect:/course/detail/" + courseno + "?error";
		} 
		return "redirect:/course/detail/" + courseno + "?success";
	}
	
	@GetMapping("/list")
	public String ApplyList(Model model, Principal principal,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "filter", defaultValue = "all") String filter) { // 페이지 파라미터 추가
		
		if(principal == null) {
			return "redirect:/member/login";
		}
		
		Member member = memberService.getMember(principal.getName());
		Pageable pageable = PageRequest.of(page, 6, Sort.by("apldate").descending());
		Page<Apply> applyPage = applyService.getActiveApplicationsByMember(member, pageable);
		
		model.addAttribute("paging", applyPage); 
		
		// 진행률 계산 추가 부분
        List<Integer> courseIds = applyPage.getContent().stream()
                .map(apply -> apply.getCourse().getCourseno())
                .collect(Collectors.toList());

        Map<Integer, Double> progressRates = progressService.getProgressRatesForCourses(member.getMemberno(), courseIds);

        model.addAttribute("progressRates", progressRates);
		
        // 필터링 처리
        List<Apply> filteredList = applyPage.getContent().stream()
                .filter(apply -> {
                    double rate = progressRates.getOrDefault(apply.getCourse().getCourseno(), 0.0);

                    if (filter.equals("progress")) {
                        return apply.getStatus() == 1 && rate < 100;
                    }
                    if (filter.equals("complete")) {
                        return apply.getStatus() == 1 && rate == 100;
                    }
                    return true; // 전체
                })
                .toList();
        
        model.addAttribute("applyList", filteredList);
        model.addAttribute("filter", filter);
        
        //퀴즈 높은 점수 map 가져오기
        Map<Integer, Integer> topScore = applyPage.getContent().stream().collect(Collectors.toMap(apply -> apply.getCourse().getCourseno(),
        		apply -> {
        			Quiz quiz = apply.getCourse().getQuiz();
        			return attemptService.getTopScore(member, quiz);
        		}));
        model.addAttribute("topScore", topScore);
        
		// 수강 중인 강좌가 있는지 확인하는 플래그 
		boolean hasActiveApplications = applyPage.getTotalElements() > 0;
        model.addAttribute("hasActiveApplications", hasActiveApplications); 

		return "myclass";
	}
	
	//취소
	@PostMapping("/cancel/{applyno}")
	public String ApplyCancel(@PathVariable("applyno") Integer applyno, Principal principal) {
		if(principal == null) {
			return "redirect:/member/login";
		}
		
		Member member = memberService.getMember(principal.getName());
		Apply apply = applyService.getApply(applyno);
		
		applyService.cancle(apply);
		return "redirect:/apply/list";
	}
}