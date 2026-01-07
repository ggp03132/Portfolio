package com.project.lms.course;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.lms.apply.Apply;
import com.project.lms.apply.ApplyService;
import com.project.lms.category.Category;
import com.project.lms.category.CategoryRepository;
import com.project.lms.category.CategoryService;
import com.project.lms.member.Member;
import com.project.lms.member.MemberService;
import com.project.lms.progress.ProgressService;
import com.project.lms.question.Question;
import com.project.lms.question.QuestionService;
import com.project.lms.review.Review;
import com.project.lms.review.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {
	private final CourseService courseService;
	private final CategoryService categoryService;
	private final MemberService memberService;
	private final CategoryRepository categoryRepository;
	private final QuestionService questionService;
	private final ReviewService reviewService;	
	private final ApplyService applyService;
	private final ProgressService progressService;

	// 전체 강좌 목록 조회 (카테고리별 리스트와 모든 강좌보기를 courselist 하나의 뷰에 구현)
	@GetMapping("/list")
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "kw", required = false) String kw,
			@RequestParam(value = "categoryno", required = false) Integer categoryno,
			@RequestParam(value = "courseno", required = false) Integer courseno) {

		Category category = null; // 기본 null

		if (categoryno != null) {
			category = categoryService.getCategory(categoryno);
		}

		// 모든 카테고리 목록 조회 및 모델 추가
		List<Category> allCategories = categoryService.getAllCategories();
		model.addAttribute("categoryList", allCategories);

		// 페이징된 강좌 목록
		Page<Course> coursePaging = courseService.getPageListByCategory(category,page, kw);
		model.addAttribute("paging", coursePaging);
		model.addAttribute("categoryno", categoryno);
		model.addAttribute("kw", kw);

		// 부트스트랩 색상 클래스 카테고리별 색상을 다르게 주기 위함
		String[] colorClasses = { "btn-primary", "btn-secondary", "btn-success", "btn-danger", "btn-warning",
				"btn-info" };

		// 모델에 담아 뷰에 전달
		model.addAttribute("colorClasses", colorClasses);

		// 전체 목록이므로 categoryno와 categoryName은 null
		model.addAttribute("categoryno", null);
		model.addAttribute("categoryName", null);

		
		return "course/course_list";
	}

	// 카테고리별 리스트
	@GetMapping("/category/{categoryno}")
	public String categoryCourse(Model model, @PathVariable("categoryno") Integer categoryno,
			@RequestParam(value = "page", defaultValue = "0") int page) {

		// 모든 카테고리 목록 조회 및 모델 추가
		List<Category> allCategories = categoryService.getAllCategories();
		model.addAttribute("categoryList", allCategories);

		// 현재 카테고리 정보 조회
		Category category = categoryRepository.findById(categoryno).orElseThrow();

		// 페이징된 강좌 목록
		Page<Course> coursePaging = courseService.getCourseByCategory(categoryno, page);

		// 부트스트랩 색상 클래스 카테고리별 색상을 다르게 주기 위함
		String[] colorClasses = { "btn-primary", "btn-secondary", "btn-success", "btn-danger", "btn-warning",
				"btn-info" };

		// 모델에 담아 뷰에 전달
		model.addAttribute("colorClasses", colorClasses);
		model.addAttribute("paging", coursePaging);
		model.addAttribute("categoryno", categoryno); // 현재 카테고리 번호
		model.addAttribute("categoryName", category.getTitle()); // 현재 카테고리 이름

		return "course/course_list";
	}

	// 강사별 강좌 리스트(나의 강좌)
	@GetMapping("/instructor/list")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public String instructorCourseList(Model model, Principal principal,
			@RequestParam(value = "page", defaultValue = "0") int page) {

		// 로그인한 강사/관리자의 ID를 가져옴
		String memberId = principal.getName();

		// MemberService를 통해 Member 객체를 조회
		Member instructor = memberService.getMember(memberId);

		// 강사 ID를 기준으로 페이징된 강좌 목록을 조회
		Page<Course> paging = courseService.getCourseByInstructor(instructor, page);

		model.addAttribute("paging", paging);
		model.addAttribute("pageTitle", "내가 등록한 강좌 목록");

		// 뷰 템플릿 이름은 'course_list'를 재활용하거나 'instructor_course_list' 등을 사용할 수 있습니다.
		return "course/course_instructor_list";
	}
	
	@GetMapping("/instructor/members")
	@PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
	public String instructorMembers(
	        @RequestParam(value = "courseName", required = false) String courseName,
	        Model model,
	        Principal principal) {

	    // 로그인한 강사
	    Member instructor = memberService.getMember(principal.getName());

	    // 강사가 올린 모든 강좌 조회
	    List<Course> courses = courseService.getCoursesByInstructor(instructor);

	    List<Apply> applies;

	    if (courseName == null || courseName.isEmpty()) {
	        applies = courses.stream()
	                         .flatMap(course -> course.getApplyList().stream())
	                         .filter(apply -> apply.getStatus() == 1)
	                         .toList();
	    } else {
	        applies = courses.stream()
	                         .filter(course -> course.getTitle().toLowerCase().contains(courseName.toLowerCase()))
	                         .flatMap(course -> course.getApplyList().stream())
	                         .filter(apply -> apply.getStatus() == 1)
	                         .toList();
	    }

	    // 진행률 계산
	    Map<String, Double> progressRates = progressService.getProgressRatesForCoursesForMembers(applies);

	    model.addAttribute("applies", applies);
	    model.addAttribute("progressRates", progressRates);

	    return "course/course_all_members";
	}
	
	// 상세조회
		@GetMapping("/detail/{courseno}")
		public String detail(Model model, @PathVariable("courseno") Integer courseno, Principal principal) {
			Course course = courseService.getCourse(courseno);
			model.addAttribute("course", course);
			List<Question> questionList = questionService.getListByCourse(course); // 강좌의 전체 질문 목록 가져오기

			List<Review> reviewList = reviewService.getReviewByCourse(course); // 강좌의 전체 리뷰 목록 가져오기
			model.addAttribute("reviewList", reviewList);
			model.addAttribute("questionList", questionList);
			// 타 강좌나 강의에 한 접근제어
			boolean loginMember = false;
			// 로그인 한 사용자가 있으면
			if (principal != null) {
				// 그 사용자의 이름을 멤버에 저장
				String member = principal.getName();
				// 역할이 강사이고 강사의 이름과 멤버에 저장한 이름이 같으면
				if (course.getInstructor() != null && member.equals(course.getInstructor().getMemberid())) {
					// 해당 멤버를 해당 강좌에 대한 등록자로 판단
					loginMember = true;
				}
			}
			
			boolean isEnrolled = false;
		    if (principal != null) {
		        Member member = memberService.getMember(principal.getName());
		        isEnrolled = applyService.isEnrolled(member, course);
		    }
		    
			model.addAttribute("loginMember", loginMember);
			model.addAttribute("isEnrolled", isEnrolled);
			return "course/course_detail";
		}

	// 카테고리 상세에서 강좌등록을 눌렀을때 띄우는 get매핑
	@GetMapping("/create/{categoryno}")
	public String courseCreate(Model model, @PathVariable("categoryno") Integer categoryno, CourseForm courseForm) {
		Category category = categoryService.getCategory(categoryno);
		model.addAttribute("category", category);
		return "course/course_create";
	}

	// 강좌 생성 post 매핑 (수정됨)
	@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
	@PostMapping("/create/{categoryno}")
	public String courseCreate(Model model, @PathVariable("categoryno") Integer categoryno,
			@Valid CourseForm courseForm, BindingResult bindingResult, Principal principal) {

		Category category = categoryService.getCategory(categoryno);

		if (bindingResult.hasErrors()) {
			model.addAttribute("category", category);
			return "course/course_create";
		}

		// 정보 조회 및 전달
		String memberid = principal.getName();
		Member instructor = memberService.getMember(memberid);

		courseService.create(courseForm.getTitle(), courseForm.getContent(), courseForm.getCourseimg(),
				courseForm.getGrade(), courseForm.getBook(), courseForm.getBookimg(), category, instructor);
		return "redirect:/course/list";
	}

	// 수정
	@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
	@GetMapping("/modify/{courseno}")
	public String courseModify(Model model, @PathVariable("courseno") Integer courseno, Principal principal) {
		Course course = courseService.getCourse(courseno);

		// 로그인한 사람의 id를 가져오는 loginMember, 강사가 null이거나 강좌의 강사 id가 같지 않으면 리스트로 리다이렉트
		String loginMember = principal.getName();
		Member member = memberService.getMember(loginMember);
		boolean isAdmin = member.getRole().name().equals("ADMIN");

		// 첫번째 if 강사 조건 확인 > 강사가 아니거나, 로그인한 강사가 아니면 --> 두번째 if 관리자 확인
		if (course.getInstructor() == null || !course.getInstructor().getMemberid().equals(loginMember)) {
			if (!isAdmin) {
				return "redirect:/course/list";
			}
		}
		CourseForm courseForm = new CourseForm();

		courseForm.setCourseno(course.getCourseno());
		courseForm.setTitle(course.getTitle());
		courseForm.setContent(course.getContent());
		courseForm.setCourseimg(course.getCourseimg());
		courseForm.setGrade(course.getGrade());
		courseForm.setBook(course.getBook());
		courseForm.setBookimg(course.getBookimg());

		model.addAttribute("courseForm", courseForm);
		model.addAttribute("course", course);
		return "course/course_modify";
	}

	@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
	@PostMapping("/modify/{courseno}")
	public String courseModify(Model model, @PathVariable("courseno") Integer courseno, @Valid CourseForm courseForm,
			BindingResult bindingResult) {
		Course course = courseService.getCourse(courseno);
		if (bindingResult.hasErrors()) {
			model.addAttribute("course", course);
			return "course/course_modify";
		}
		courseService.modify(course, courseForm.getTitle(), courseForm.getContent(), courseForm.getCourseimg(),
				courseForm.getGrade(), courseForm.getBook(), courseForm.getBookimg(), course.getCategory());
		return String.format("redirect:/course/detail/%s", courseno);
	}

	// 삭제
	@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
	@PostMapping("/delete/{courseno}")
	public String courseDelete(@PathVariable("courseno") Integer courseno, Model model) {
		Course course = courseService.getCourse(courseno);
		
		try {
			courseService.delete(course);
		} catch (IllegalStateException e) {
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("course", course);
			return "redirect:/course/instructor/list";
		}
		return "redirect:/course/instructor/list";
	}
}