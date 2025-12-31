package com.project.lms.useranswer;

import com.project.lms.attempt.Attempt;
import com.project.lms.quizquestion.QuizQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserAnswer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer useranswerno;
	
	@Column(nullable = false)
	private String answer;	
	private Boolean iscorrect;
	
	@ManyToOne
	private Attempt attempt;
	
	@ManyToOne
	private QuizQuestion quizQuestion;
}
