package com.project.lms.review;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.lms.DataNotFoundException;
import com.project.lms.apply.ApplyRepository;
import com.project.lms.course.Course;
import com.project.lms.lesson.Lesson;
import com.project.lms.member.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	
	private final ApplyRepository applyRepository;
	private final ReviewRepository reviewRepository;

	public Page<Review> getList(int page){
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("credate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		return reviewRepository.findAll(pageable);
	}
	
	public List<Review> getList(){
		return reviewRepository.findAll(Sort.by(Sort.Direction.DESC, "reviewno")); //최신순
	}
	
	public Review getReview(Integer reviewno) {
		Optional<Review> review = reviewRepository.findById(reviewno);
		if(review.isPresent()) {
			return review.get();
		} else {
			throw new DataNotFoundException("not found");
		}
	}
    
	// 특정 강의(Lesson)의 리뷰 목록 조회
    public List<Review> getReviewsByLesson(Lesson lesson) {
        return reviewRepository.findByLesson(lesson);
    }

    // 특정 회원(Member)이 작성한 리뷰 목록 조회 (선택)
    public List<Review> getReviewsByMember(Member member) {
        return reviewRepository.findByMember(member);
    }
    
    // 특정 강좌(Course)의 리뷰 목록 조회
    public List<Review> getReviewByCourse(Course course) {
        return reviewRepository.findByCourse(course);
    }
    public List<Review> getRecentReviews(int instructorId, int limit) {
	    return reviewRepository.findRecentReviewsByInstructor(
	        instructorId,
	        PageRequest.of(0, limit)
	    );
	}
    //생성
    public void createReview(Member member, Course course, String content, Integer rating) {
        // 수강신청 여부 확인
        boolean isEnrolled = applyRepository.existsByMemberAndCourseAndStatus(member, course, 1);
        if (!isEnrolled) {
            throw new IllegalStateException("수강신청하지 않은 강좌에는 리뷰를 작성할 수 없습니다.");
        }

        // 이미 리뷰 작성한 적 있는지 체크
        boolean alreadyReviewed = reviewRepository.existsByMemberAndCourse(member, course);
        if (alreadyReviewed) {
            throw new IllegalStateException("이미 리뷰를 작성한 강좌입니다.");
        }
        
		Review review = new Review();
		review.setContent(content);
		review.setCredate(LocalDateTime.now());
		review.setModdate(LocalDateTime.now());
		review.setMember(member);
		review.setCourse(course);
		review.setRating(rating);
		reviewRepository.save(review);
    }
    
    public void modify(Review review, Integer rating, String content) {
		review.setRating(rating);
		review.setContent(content);
		review.setModdate(LocalDateTime.now());
		reviewRepository.save(review);
	}
    
    public void delete(Review review) {
		reviewRepository.delete(review);
	}
}
