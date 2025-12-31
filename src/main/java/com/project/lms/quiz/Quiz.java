package com.project.lms.quiz;

import java.util.List;

import com.project.lms.attempt.Attempt;
import com.project.lms.course.Course;
import com.project.lms.quizquestion.QuizQuestion;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Quiz {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer quizno;
	
	@Column (length = 100, nullable = false)
	private String title;
	
	@Column(nullable = false)
	private Integer totalpoint; //퀴즈의 만점 점수 ex) 1강의 퀴즈 최대 점수 = 10점 > totalpoint = 10
	
	@OneToOne
	@JoinColumn(name = "courseno")
	private Course course;
	
	@OneToMany(mappedBy = "quiz", cascade = CascadeType.REMOVE,fetch = FetchType.EAGER)
	private List<QuizQuestion> quizquestionList;
	
	@OneToMany(mappedBy = "quiz", cascade = CascadeType.REMOVE)
	private List<Attempt> attemptList;
}