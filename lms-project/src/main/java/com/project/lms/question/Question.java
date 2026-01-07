package com.project.lms.question;

import java.time.LocalDateTime;
import java.util.List;

import com.project.lms.answer.Answer;
import com.project.lms.course.Course;
import com.project.lms.lesson.Lesson;
import com.project.lms.member.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Question {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer questionno;
	
	@Column(length = 45, nullable = false)
	private String title;
	
	@Column(length = 500, nullable = false)
	private String content;
	
	private LocalDateTime credate;
	private LocalDateTime moddate;
	
	@ManyToOne
	private Member member;
	
	@OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
	private List<Answer> answerList;
	
	@ManyToOne
	private Lesson lesson;
	@ManyToOne
	private Course course;
}