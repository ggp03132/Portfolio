package com.project.lms.question;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.lms.course.Course;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
	
	Question findByTitle(String title);
	Question findByContent(String content);
	Question findByTitleAndContent(String title, String content);
	Question findByTitleOrContent(String title, String content);
	List<Question> findByTitleLike(String title);
	List<Question> findByCourse(Course course); //강좌에 있는 모든 질문
	@Query("""
		    SELECT q
		    FROM Question q
		    JOIN q.course c
		    WHERE c.instructor.memberno = :instructorId
		    ORDER BY q.credate DESC
		""")
	List<Question> findRecentQuestionsByInstructor(@Param("instructorId") int instructorId, Pageable pageable);
	Page<Question> findAll(Pageable pageable);
	Page<Question> findAll(Specification<Question> spec, Pageable pageable);
	
}