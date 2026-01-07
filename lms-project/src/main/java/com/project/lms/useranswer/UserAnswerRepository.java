package com.project.lms.useranswer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.lms.attempt.Attempt;
import com.project.lms.quiz.Quiz;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Integer> {
	List<UserAnswer> findByAttempt (Attempt attempt);
	
	List<UserAnswer> findByAttempt_Quiz (Quiz quiz); //퀴즈의 기록 확인
	
	@Query("SELECT ua FROM UserAnswer ua " +
		       "WHERE ua.quizQuestion.quiz.course.courseno = :courseno " +
		       "ORDER BY ua.attempt.member.name, ua.quizQuestion.quizquestionno")
	List<UserAnswer> findByCourse(@Param("courseno") Integer courseno);
}
