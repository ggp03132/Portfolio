package com.project.lms.quizquestion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.lms.quiz.Quiz;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
	 List<QuizQuestion> findByQuiz(Quiz quiz);
	 
}