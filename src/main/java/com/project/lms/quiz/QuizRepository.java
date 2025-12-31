package com.project.lms.quiz;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {
	List<Quiz> findByCourse_Courseno(Integer courseno);
}
