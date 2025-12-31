package com.project.lms.course;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.project.lms.DataNotFoundException;
import com.project.lms.category.Category;
import com.project.lms.member.Member;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {
	private final CourseRepository cr;

	// 테스트
	public Page<Course> getPageListByCategory(Category category, int page, String kw) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("credate"));
		Pageable pageable = PageRequest.of(page, 6, Sort.by(sorts));

		Specification<Course> spec = (Root<Course> q, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			query.distinct(true);
			// Predicate predicate = cb.equal(q.get("category"), category); // 카테고리 일치 조건

			Predicate predicate = cb.conjunction(); // 기본 조건: 항상 true

			// 카테고리 조건 (null일 경우 전체 보기)
			if (category != null) {
				predicate = cb.and(predicate, cb.equal(q.get("category"), category));
			}

			if (kw != null && !kw.trim().isEmpty()) {
				Predicate keyword = cb.or(cb.like(q.get("title"), "%" + kw + "%"),
						cb.like(q.get("content"), "%" + kw + "%"));
				return cb.and(predicate, keyword); // 카테고리 + 검색어
			} else {
				return predicate; // 검색어 없으면 카테고리만 필터링
			}
		};

		return cr.findAll(spec, pageable);
	}

	// 검색
	private Specification<Course> search(String kw) {
		return new Specification<Course>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Course> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
				query.distinct(true); // 중복제거
				Join<Course, Category> c = q.join("category", JoinType.INNER); // INNER는 Join<a,b>에서 a랑 b 둘 다 일치하는 데이터
				return cb.or(cb.like(q.get("title"), "%" + kw + "%"), // 제목검색
						cb.like(q.get("content"), "%" + kw + "%")); // 내용검색
			}
		};
	}

	// 카테고리별 리스트
	public Page<Course> getCourseByCategory(Integer categoryno, int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("credate")); // 최신순 정렬
		Pageable pageable = PageRequest.of(page, 6, Sort.by(sorts)); // 페이지당 6개

		// Specification을 사용하여 카테고리 번호로 필터링
		Specification<Course> spec = (Root<Course> q, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			// categoryno와 Course 엔티티의 category 엔티티의 categoryno가 일치하는지 비교
			return cb.equal(q.get("category").get("categoryno"), categoryno);
		};

		return cr.findAll(spec, pageable);
	}

	// 페이징
	public Page<Course> getList(int page, String kw) {
		List<Sort.Order> sorts = new ArrayList<>();
		// 생성일 기준 내림차순 정렬
		sorts.add(Sort.Order.desc("credate"));
		// 페이징 변수 (6개씩 페이징)
		Pageable pageable = PageRequest.of(page, 6, Sort.by(sorts));
		Specification<Course> spec = search(kw);
		return cr.findAll(spec, pageable);
	}

	// 강사별 강좌 리스트(나의 강좌)
	public Page<Course> getCourseByInstructor(Member instructor, int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		// 최신순 정렬
		sorts.add(Sort.Order.desc("credate"));

		// 페이지당 6개씩 설정
		Pageable pageable = PageRequest.of(page, 6, Sort.by(sorts));

		return cr.findByInstructor(instructor, pageable);
	}
	
	// 강사별 강좌 리스트(메인페이지)
	public List<Course> getCoursesByInstructor(Member instructor) {
		return cr.findByInstructor(instructor);
	}

	// 상세조회
	public Course getCourse(Integer courseno) {
		Optional<Course> course = cr.findById(courseno);
		if (course.isPresent()) {
			return course.get();
		} else {
			throw new DataNotFoundException("강좌가 존재하지 않습니다");
		}
	}

	// 생성
	public void create(String title, String content, String courseimg, Integer grade, String book, String bookimg,
			Category category, Member instructor) {
		Course course = new Course();
		course.setTitle(title);
		course.setContent(content);
		course.setCourseimg(courseimg);
		course.setGrade(grade);
		course.setBook(book);
		course.setBookimg(bookimg);
		course.setCredate(LocalDateTime.now());
		course.setModdate(LocalDateTime.now());
		course.setCategory(category);
		course.setInstructor(instructor);
		cr.save(course);
	}

	// 수정
	public void modify(Course course, String title, String content, String courseimg, Integer grade, String book,
			String bookimg, Category category) {
		course.setTitle(title);
		course.setContent(content);
		course.setCourseimg(courseimg);
		course.setGrade(grade);
		course.setBook(book);
		course.setBookimg(bookimg);
		course.setModdate(LocalDateTime.now());
		course.setCategory(category);
		cr.save(course);
	}

	// 삭제
	public void delete(Course course) {
		
		boolean checkApply = course.getApplyList().stream().anyMatch(apply -> apply.getStatus() ==1);
		
		if (checkApply) {
			throw new IllegalStateException("수강 중인 학생이 있어서 삭제가 불가능합니다.");
		}
		
		cr.delete(course);
	}

}
