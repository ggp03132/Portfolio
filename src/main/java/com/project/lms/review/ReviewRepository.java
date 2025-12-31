package com.project.lms.review;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.lms.course.Course;
import com.project.lms.lesson.Lesson;
import com.project.lms.member.Member;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	List<Review> findByLesson(Lesson lesson);
	List<Review> findByMember(Member member);
	List<Review> findByCourse(Course course);
	@Query("""
		    SELECT r
		    FROM Review r
		    JOIN r.course c
		    WHERE c.instructor.memberno = :instructorId
		    ORDER BY r.credate DESC
		""")
	List<Review> findRecentReviewsByInstructor(@Param("instructorId") int instructorId, Pageable pageable);
    Page<Review> findAll(Pageable pageable);
    Page<Review> findAll(Specification<Review> spec, Pageable pageable);
    
    boolean existsByMemberAndCourse(Member member, Course course);
}
