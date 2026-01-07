package com.project.lms.quizquestion;

import java.util.List;

import com.project.lms.quiz.Quiz;
import com.project.lms.useranswer.UserAnswer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class QuizQuestion {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer quizquestionno;
	
	@Column(length = 100, nullable=false)
	private String content;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private QuizType type;  // 예: "O", "X"
	
	@Column(nullable = false)
	private Integer point=1;
	
	@Column(length = 45, nullable=false)
	private String answer;
	
	@ManyToOne
	@JoinColumn(name="quizno")
	private Quiz quiz;
	
	@OneToMany(mappedBy = "quizQuestion", cascade = CascadeType.REMOVE)
	private List<UserAnswer> useranswerList;
}